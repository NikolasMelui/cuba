/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.cuba.web.gui.components;

import com.haulmont.bali.events.Subscription;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.sys.FrameImplementation;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractComponent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class WebAbstractOrderedLayout<T extends com.vaadin.ui.CssLayout> extends WebAbstractComponent<T>
        implements OrderedContainer, Component.BelongToFrame, Component.HasCaption, Component.HasIcon,
        LayoutClickNotifier, ShortcutNotifier {

    protected List<Component> ownComponents = new ArrayList<>();
    protected LayoutEvents.LayoutClickListener layoutClickListener;
    protected Map<ShortcutAction, ShortcutListener> shortcuts;

    @Override
    public void add(Component childComponent) {
        add(childComponent, ownComponents.size());
    }

    @Override
    public void add(Component childComponent, int index) {
        if (childComponent.getParent() != null && childComponent.getParent() != this) {
            throw new IllegalStateException("Component already has parent");
        }

        if (ownComponents.contains(childComponent)) {
            int existingIndex = component.getComponentIndex(WebComponentsHelper.getComposition(childComponent));
            if (index > existingIndex) {
                index--;
            }

            remove(childComponent);
        }

        com.vaadin.ui.Component vComponent = WebComponentsHelper.getComposition(childComponent);
        component.addComponent(vComponent, index);

        if (frame != null) {
            if (childComponent instanceof BelongToFrame
                    && ((BelongToFrame) childComponent).getFrame() == null) {
                ((BelongToFrame) childComponent).setFrame(frame);
            } else {
                ((FrameImplementation) frame).registerComponent(childComponent);
            }
        }

        if (index == ownComponents.size()) {
            ownComponents.add(childComponent);
        } else {
            ownComponents.add(index, childComponent);
        }

        childComponent.setParent(this);
    }

    @Override
    public int indexOf(Component component) {
        return ownComponents.indexOf(component);
    }

    @Nullable
    @Override
    public Component getComponent(int index) {
        return ownComponents.get(index);
    }

    @Override
    public void remove(Component childComponent) {
        component.removeComponent(WebComponentsHelper.getComposition(childComponent));
        ownComponents.remove(childComponent);

        childComponent.setParent(null);
    }

    @Override
    public void removeAll() {
        component.removeAllComponents();

        Component[] components = ownComponents.toArray(new Component[ownComponents.size()]);
        ownComponents.clear();

        for (Component childComponent : components) {
            childComponent.setParent(null);
        }
    }

    @Override
    public void setFrame(Frame frame) {
        super.setFrame(frame);

        if (frame != null) {
            for (Component childComponent : ownComponents) {
                if (childComponent instanceof BelongToFrame
                        && ((BelongToFrame) childComponent).getFrame() == null) {
                    ((BelongToFrame) childComponent).setFrame(frame);
                }
            }
        }
    }

    @Nullable
    @Override
    public Component getOwnComponent(String id) {
        Preconditions.checkNotNullArgument(id);

        return ownComponents.stream()
                .filter(component -> Objects.equals(id, component.getId()))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public Component getComponent(String id) {
        return ComponentsHelper.getComponent(this, id);
    }

    @Override
    public Collection<Component> getOwnComponents() {
        return Collections.unmodifiableCollection(ownComponents);
    }

    @Override
    public Stream<Component> getOwnComponentsStream() {
        return ownComponents.stream();
    }

    @Override
    public Collection<Component> getComponents() {
        return ComponentsHelper.getComponents(this);
    }

    @Override
    public String getDescription() {
        return getComposition().getDescription();
    }

    @Override
    public void setDescription(String description) {
        if (getComposition() instanceof AbstractComponent) {
            ((AbstractComponent) getComposition()).setDescription(description);
        }
    }

    @Override
    public Subscription addLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        if (layoutClickListener == null) {
            layoutClickListener = event -> {
                Component childComponent = findChildComponent(this, event.getChildComponent());
                MouseEventDetails mouseEventDetails = WebWrapperUtils.toMouseEventDetails(event);

                LayoutClickEvent layoutClickEvent = new LayoutClickEvent(this, childComponent, mouseEventDetails);

                publish(LayoutClickEvent.class, layoutClickEvent);
            };
            component.addLayoutClickListener(layoutClickListener);
        }

        getEventHub().subscribe(LayoutClickEvent.class, listener);

        return () -> removeLayoutClickListener(listener);
    }

    protected Component findChildComponent(ComponentContainer layout, com.vaadin.ui.Component clickedComponent) {
        for (Component component : layout.getComponents()) {
            if (component.unwrapComposition(com.vaadin.ui.Component.class) == clickedComponent) {
                return component;
            }
        }
        return null;
    }
    
    @Override
    public void removeLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        unsubscribe(LayoutClickEvent.class, listener);

        if (!hasSubscriptions(LayoutClickEvent.class)) {
            component.removeLayoutClickListener(layoutClickListener);
            layoutClickListener = null;
        }
    }

    @Override
    public void addShortcutAction(ShortcutAction action) {
        KeyCombination keyCombination = action.getShortcutCombination();
        com.vaadin.event.ShortcutListener shortcut =
                new ContainerShortcutActionWrapper(action, this, keyCombination);
        component.addShortcutListener(shortcut);

        if (shortcuts == null) {
            shortcuts = new HashMap<>(4);
        }
        shortcuts.put(action, shortcut);
    }

    @Override
    public void removeShortcutAction(ShortcutAction action) {
        if (shortcuts != null) {
            component.removeShortcutListener(shortcuts.remove(action));

            if (shortcuts.isEmpty()) {
                shortcuts = null;
            }
        }
    }

    @Override
    public void attached() {
        super.attached();

        for (Component component : ownComponents) {
            ((AttachNotifier) component).attached();
        }
    }

    @Override
    public void detached() {
        super.detached();

        for (Component component : ownComponents) {
            ((AttachNotifier) component).detached();
        }
    }
}