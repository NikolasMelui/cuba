/*
 * Copyright (c) 2008-2018 Haulmont.
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
 */

package spec.cuba.web.datacontext

import com.haulmont.bali.util.Dom4j
import com.haulmont.cuba.core.global.ViewRepository
import com.haulmont.cuba.gui.model.*
import com.haulmont.cuba.gui.model.impl.NoopDataContext
import com.haulmont.cuba.gui.model.impl.ScreenDataImpl
import com.haulmont.cuba.gui.model.impl.ScreenDataXmlLoader
import com.haulmont.cuba.gui.model.impl.CollectionLoaderImpl
import com.haulmont.cuba.gui.model.impl.InstanceLoaderImpl
import com.haulmont.cuba.security.entity.User
import com.haulmont.cuba.web.testmodel.sales.Order
import com.haulmont.cuba.web.testmodel.sales.OrderLine
import com.haulmont.cuba.web.testmodel.sales.Product
import com.haulmont.cuba.web.testmodel.sales.ProductTag
import org.dom4j.Document
import spec.cuba.web.WebSpec

class ScreenDataTest extends WebSpec {

    @Override
    void setup() {
    }

    def "containers without loaders"() {
        def xml = '''
            <data>
                <instance id="userCont"
                          class="com.haulmont.cuba.security.entity.User" view="user.edit"/>
                
                <collection id="usersCont"
                            class="com.haulmont.cuba.security.entity.User" view="user.browse"/>
            </data>
            '''
        Document document = Dom4j.readDocument(xml)
        ScreenData screenData = new ScreenDataImpl()
        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        screenDataLoader.load(screenData, document.rootElement, null)
        DataContext dataContext = screenData.dataContext
        InstanceContainer<User> userCont = screenData.getContainer('userCont')
        CollectionContainer<User> usersCont = screenData.getContainer('usersCont')

        then:

        dataContext != null
        userCont != null
        usersCont != null
        userCont.view == viewRepository.getView(User, 'user.edit')
        usersCont.view == viewRepository.getView(User, 'user.browse')
    }

    def "containers with loaders"() {
        def xml = '''
            <data>
                <instance id="userCont"
                          class="com.haulmont.cuba.security.entity.User" view="user.edit">
                          
                    <loader id="userLoader">
                        <query>
                            select u from sec$User u where u.id = 1
                        </query>
                    </loader>
                </instance>

                <collection id="usersCont"
                            class="com.haulmont.cuba.security.entity.User" view="user.browse">
            
                    <loader id="usersLoader">
                        <query>
                            select u from sec$User u
                        </query>
                    </loader>
                </collection>
                
                <collection id="usersCont1"
                            class="com.haulmont.cuba.security.entity.User" view="user.browse">
            
                    <loader>
                        <query>
                            select u from sec$User u
                        </query>
                    </loader>
                </collection>
                
                <keyValueCollection id="userInfoCont">
                    <properties>
                        <property name="login"/>
                        <property name="name"/>
                    </properties>
                    <loader id="userInfoLoader">
                        <query>
                            select u.login, u.name from sec$User u where u.login like :login
                        </query>
                    </loader>
                </keyValueCollection>
                
                <keyValueInstance id="userInfoInstanceCont">
                    <properties>
                        <property name="login"/>
                        <property name="name"/>
                    </properties>
                    <loader id="userInfoInstanceLoader">
                        <query>
                            select u.login, u.name from sec$User u where u.id = 1
                        </query>
                    </loader>
                </keyValueInstance>
                
                
            </data>
            '''
        Document document = Dom4j.readDocument(xml)
        ScreenData screenData = new ScreenDataImpl()
        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        screenDataLoader.load(screenData, document.rootElement, null)
        DataContext dataContext = screenData.dataContext
        InstanceContainer<User> userCont = screenData.getContainer('userCont')
        InstanceLoader<User> userLoader = screenData.getLoader('userLoader')
        CollectionContainer<User> usersCont = screenData.getContainer('usersCont')
        CollectionContainer<User> usersCont1 = screenData.getContainer('usersCont1')
        CollectionLoader<User> usersLoader = screenData.getLoader('usersLoader')
        KeyValueCollectionContainer userInfoCont = screenData.getContainer('userInfoCont')
        KeyValueCollectionLoader userInfoLoader = screenData.getLoader('userInfoLoader')
        KeyValueContainer userInfoInstanceCont = screenData.getContainer('userInfoInstanceCont')
        KeyValueInstanceLoader userInfoInstanceLoader = screenData.getLoader('userInfoInstanceLoader')

        then:

        userCont != null
        userLoader != null
        userCont instanceof HasLoader
        ((HasLoader) userCont).loader == userLoader
        userLoader.dataContext == dataContext
        userLoader.container == userCont
        userLoader.query == 'select u from sec$User u where u.id = 1'
        userLoader.softDeletion

        usersCont != null
        usersLoader != null
        usersCont instanceof HasLoader
        ((HasLoader) usersCont).loader == usersLoader
        usersLoader.dataContext == dataContext
        usersLoader.container == usersCont
        usersLoader.query == 'select u from sec$User u'
        usersLoader.softDeletion
        usersLoader.firstResult == 0
        usersLoader.maxResults == Integer.MAX_VALUE
        !usersLoader.cacheable

        screenData.getLoaderIds().find { String id -> screenData.getLoader(id) == usersCont1.loader } != null

        userInfoLoader.container == userInfoCont
        userInfoCont instanceof HasLoader
        ((HasLoader) userInfoCont).loader == userInfoLoader
        userInfoCont.getEntityMetaClass().getProperty('login') != null
        userInfoCont.getEntityMetaClass().getProperty('name') != null
        userInfoLoader.getContainer() == userInfoCont
        userInfoLoader.getQuery() == 'select u.login, u.name from sec$User u where u.login like :login'

        userInfoInstanceLoader.container == userInfoInstanceCont
        userInfoInstanceCont instanceof HasLoader
        ((HasLoader) userInfoInstanceCont).loader == userInfoInstanceLoader
        userInfoInstanceCont.getEntityMetaClass().getProperty('login') != null
        userInfoInstanceCont.getEntityMetaClass().getProperty('name') != null
        userInfoInstanceLoader.getContainer() == userInfoInstanceCont
        userInfoInstanceLoader.getQuery() == 'select u.login, u.name from sec$User u where u.id = 1'
    }

    def "loader options"() {
        def xml = '''
            <data>
                <instance id="userCont"
                          class="com.haulmont.cuba.security.entity.User" view="user.edit">
                          
                    <loader id="userLoader" entityId="60885987-1b61-4247-94c7-dff348347f93" softDeletion="false"
                            dynamicAttributes="true"/>
                </instance>

                <collection id="usersCont"
                            class="com.haulmont.cuba.security.entity.User" view="user.browse">
            
                    <loader id="usersLoader" softDeletion="false" firstResult="100" maxResults="1000" cacheable="true"
                            dynamicAttributes="true">
                        <query>
                            select u from sec$User u
                        </query>
                    </loader>
                </collection>

                <keyValueCollection id="userInfoCont">
                    <properties>
                        <property name="login"/>
                        <property name="name"/>
                    </properties>
                    <loader id="userInfoLoader" store="foo" softDeletion="false" firstResult="100" maxResults="1000">
                        <query>
                            select u.login, u.name from sec$User u where u.login like :login
                        </query>
                    </loader>
                </keyValueCollection>

                <keyValueInstance id="userInfoInstanceCont">
                    <properties>
                        <property name="login"/>
                        <property name="name"/>
                    </properties>
                    <loader id="userInfoInstanceLoader" store="foo" softDeletion="false">
                        <query>
                            select u.login, u.name from sec$User u where u.id = 1
                        </query>
                    </loader>
                </keyValueInstance>
            </data>
            '''
        Document document = Dom4j.readDocument(xml)
        ScreenData screenData = new ScreenDataImpl()
        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        screenDataLoader.load(screenData, document.rootElement, null)
        InstanceLoader<User> userLoader = screenData.getLoader('userLoader')
        CollectionLoader<User> usersLoader = screenData.getLoader('usersLoader')
        KeyValueCollectionLoader userInfoLoader = screenData.getLoader('userInfoLoader')
        KeyValueInstanceLoader userInfoInstanceLoader = screenData.getLoader('userInfoInstanceLoader')

        then:

        userLoader.entityId == UUID.fromString('60885987-1b61-4247-94c7-dff348347f93')
        !userLoader.softDeletion
        userLoader.loadDynamicAttributes
        ((InstanceLoaderImpl) userLoader).createLoadContext().view == cont.getBean(ViewRepository).getView(User, 'user.edit')

        !usersLoader.softDeletion
        usersLoader.firstResult == 100
        usersLoader.maxResults == 1000
        usersLoader.cacheable
        usersLoader.loadDynamicAttributes
        ((CollectionLoaderImpl) usersLoader).createLoadContext().view == cont.getBean(ViewRepository).getView(User, 'user.browse')

        !userInfoLoader.softDeletion
        userInfoLoader.firstResult == 100
        userInfoLoader.maxResults == 1000
        userInfoLoader.storeName == 'foo'

        !userInfoInstanceLoader.softDeletion
        userInfoInstanceLoader.storeName == 'foo'
    }

    def "nested containers"() {

        def order1 = new Order(number: '111')
        def tag1 = new ProductTag(name: 't1')
        def tag2 = new ProductTag(name: 't2')
        def tag3 = new ProductTag(name: 't3')
        def product1 = new Product(name: 'p1', tags: [tag1, tag2])
        def product2 = new Product(name: 'p2', tags: [tag2, tag3])
        def product3 = new Product(name: 'p3', tags: [tag3])
        def line1 = new OrderLine(order: order1, product: product1)
        def line2 = new OrderLine(order: order1, product: product2)
        def line3 = new OrderLine(order: order1, product: product3)
        order1.orderLines = [line1, line2]

        def xml = '''
            <data>
                <instance id="orderCont"
                          class="com.haulmont.cuba.web.testmodel.sales.Order">
                          
                    <collection id="linesCont" property="orderLines">
                        <instance id="productCont" property="product">
                            <collection id="tagsCont" property="tags"/>
                        </instance>
                    </collection>
                </instance>
            </data>
            '''
        Document document = Dom4j.readDocument(xml)
        ScreenData screenData = new ScreenDataImpl()
        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        screenDataLoader.load(screenData, document.rootElement, null)
        InstanceContainer<Order> orderCont = screenData.getContainer('orderCont')
        CollectionContainer<OrderLine> linesCont = screenData.getContainer('linesCont')
        InstanceContainer<Product> productCont = screenData.getContainer('productCont')
        CollectionContainer<ProductTag> tagsCont = screenData.getContainer('tagsCont')

        then:

        linesCont != null
        productCont != null
        tagsCont != null

        when:

        orderCont.item = order1

        then:

        linesCont.items == [line1, line2]

        when:

        linesCont.item = line1

        then:

        productCont.item == product1
        tagsCont.items == [tag1, tag2]

        when: "replacing the collection value"

        orderCont.item.orderLines = [line3, line2]

        then:

        linesCont.items == [line3, line2]
    }

    def "nested collection containers"() {

        def order1 = new Order(number: '111')
        def line1 = new OrderLine(order: order1)
        def line2 = new OrderLine(order: order1)
        order1.orderLines = [line1, line2]

        def xml = '''
            <data>
                <collection id="ordersCont"
                          class="com.haulmont.cuba.web.testmodel.sales.Order">
                          
                    <collection id="linesCont" property="orderLines"/>
                </collection>
            </data>
            '''
        Document document = Dom4j.readDocument(xml)
        ScreenData screenData = new ScreenDataImpl()
        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        screenDataLoader.load(screenData, document.rootElement, null)
        CollectionContainer<Order> ordersCont = screenData.getContainer('ordersCont')
        CollectionContainer<OrderLine> linesCont = screenData.getContainer('linesCont')

        then:

        ordersCont != null
        linesCont != null

        when:

        ordersCont.items = [order1]
        ordersCont.item = order1

        then:

        linesCont.items == [line1, line2]
    }

    def "read-only data context"() {
        def xml = '''
            <data readOnly="true">
                <instance id="userCont"
                          class="com.haulmont.cuba.security.entity.User" view="user.edit">
                    <loader/>
                </instance>
            </data>
            '''
        Document document = Dom4j.readDocument(xml)
        ScreenData screenData = new ScreenDataImpl()
        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        screenDataLoader.load(screenData, document.rootElement, null)
        DataContext dataContext = screenData.dataContext

        then:

        dataContext instanceof NoopDataContext
    }

    def "containers in fragments"() {

        def xml = '''
            <data>
                <instance id="orderCont"
                          class="com.haulmont.cuba.web.testmodel.sales.Order">
                          
                    <collection id="linesCont" property="orderLines">
                        <instance id="productCont" property="product">
                            <collection id="tagsCont" property="tags"/>
                        </instance>
                    </collection>
                </instance>
                
                <collection id="ordersCont" class="com.haulmont.cuba.web.testmodel.sales.Order" view="_local">
                    <loader id="ordersLd"/>
                </collection>
            </data>
            '''

        def xmlA = '''
            <data>
                <instance id="orderCont" provided="true"
                          class="com.haulmont.cuba.web.testmodel.sales.Order">
                          
                    <collection id="linesCont" property="orderLines" provided="true">
                        <instance id="productCont" property="product" provided="true"/>
                    </collection>
                </instance>
                
                <collection id="tagsCont" class="com.haulmont.cuba.web.testmodel.sales.ProductTag" view="_local"/>
                
                <instance id="orderContA" class="com.haulmont.cuba.web.testmodel.sales.Order">
                    <collection id="linesContA" class="" property="orderLines">
                        <instance id="productContA" property="product">
                            <collection id="tagsContA" property="tags"/>
                        </instance>
                    </collection>
                </instance>

                <collection id="ordersCont" class="com.haulmont.cuba.web.testmodel.sales.Order" view="_local"
                            provided="true">
                    <loader id="ordersLd" provided="true"/>
                </collection>
            </data>
            '''

        ScreenDataXmlLoader screenDataLoader = cont.getBean(ScreenDataXmlLoader)

        when:

        ScreenData screenData = new ScreenDataImpl()
        screenDataLoader.load(screenData, Dom4j.readDocument(xml).rootElement, null)
        InstanceContainer<Order> orderCont = screenData.getContainer('orderCont')
        CollectionContainer<OrderLine> linesCont = screenData.getContainer('linesCont')
        InstanceContainer<Product> productCont = screenData.getContainer('productCont')
        CollectionContainer<ProductTag> tagsCont = screenData.getContainer('tagsCont')
        CollectionLoader<Order> ordersLd = screenData.getLoader("ordersLd")

        then:

        orderCont != null
        linesCont != null
        productCont != null
        tagsCont != null
        ordersLd != null

        when:

        ScreenData screenDataA = new ScreenDataImpl()
        screenDataLoader.load(screenDataA, Dom4j.readDocument(xmlA).rootElement, screenData)

        InstanceContainer<Order> orderCont1 = screenDataA.getContainer('orderCont')
        CollectionContainer<OrderLine> linesCont1 = screenDataA.getContainer('linesCont')
        InstanceContainer<Product> productCont1 = screenDataA.getContainer('productCont')

        CollectionContainer<ProductTag> tagsCont1 = screenDataA.getContainer('tagsCont')

        CollectionLoader<Order> ordersLd1 = screenDataA.getLoader("ordersLd")

        InstanceContainer<Order> orderContA = screenDataA.getContainer('orderContA')
        CollectionContainer<OrderLine> linesContA = screenDataA.getContainer('linesContA')
        InstanceContainer<Product> productContA = screenDataA.getContainer('productContA')
        CollectionContainer<ProductTag> tagsContA = screenDataA.getContainer('tagsContA')

        then:

        orderCont1.is(orderCont)
        linesCont1.is(linesCont)
        productCont1.is(productCont)
        ordersLd1.is(ordersLd)

        !tagsCont1.is(tagsCont)

        !orderContA.is(orderCont)
        !linesContA.is(linesCont)
        !productContA.is(productCont)
        !tagsContA.is(tagsCont)
    }
}
