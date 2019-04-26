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

package spec.cuba.core.data_manager

import com.haulmont.cuba.core.entity.contracts.Id
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.EntityStates
import com.haulmont.cuba.core.global.LoadContext
import com.haulmont.cuba.core.global.MetadataTools
import com.haulmont.cuba.core.global.ValueLoadContext
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.core.sys.AppContext
import com.haulmont.cuba.security.entity.Group
import com.haulmont.cuba.security.entity.User
import com.haulmont.cuba.testmodel.sales_1.OrderLine
import com.haulmont.cuba.testmodel.sales_1.Product
import com.haulmont.cuba.testsupport.TestContainer
import com.haulmont.cuba.testsupport.TestSupport
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class DataManagerTest extends Specification {

    @Shared @ClassRule
    public TestContainer cont = TestContainer.Common.INSTANCE

    private DataManager dataManager

    void setup() {
        dataManager = AppBeans.get(DataManager)
    }

    def "loadList query parameter without implicit conversion"() {
        def group = dataManager.load(LoadContext.create(Group).setId(TestSupport.COMPANY_GROUP_ID))

        LoadContext.Query query

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference object"

        query = LoadContext.createQuery('select u from sec$User u where u.group = :group')
        query.setParameter('group', group)
        def users = dataManager.loadList(LoadContext.create(User).setQuery(query).setView('user.browse'))

        then: "ok"

        users.size() > 0

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference id"

        query = LoadContext.createQuery('select u from sec$User u where u.group.id = :groupId')
        query.setParameter('groupId', group.id)
        users = dataManager.loadList(LoadContext.create(User).setQuery(query).setView('user.browse'))

        then: "ok"

        users.size() > 0

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "wrong condition by reference id"

        query = LoadContext.createQuery('select u from sec$User u where u.group.id = :group')
        query.setParameter('group', group)
        users = dataManager.loadList(LoadContext.create(User).setQuery(query).setView('user.browse'))

        then: "fail"

        thrown(IllegalArgumentException)
    }

    def "loadList query parameter without implicit conversion - legacy behavior"() {
        def group = dataManager.load(LoadContext.create(Group).setId(TestSupport.COMPANY_GROUP_ID))

        LoadContext.Query query

        AppContext.setProperty('cuba.implicitConversionOfJpqlParams', 'true')

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference object, no implicit conversion"

        query = LoadContext.createQuery('select u from sec$User u where u.group = :group')
        query.setParameter('group', group, false) // no implicit conversion
        def users = dataManager.loadList(LoadContext.create(User).setQuery(query).setView('user.browse'))

        then: "ok"

        users.size() > 0

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference object, implicit conversion"

        query = LoadContext.createQuery('select u from sec$User u where u.group = :group')
        query.setParameter('group', group)
        dataManager.loadList(LoadContext.create(User).setQuery(query).setView('user.browse'))

        then: "fail"

        thrown(IllegalArgumentException)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference id, implicit conversion"

        query = LoadContext.createQuery('select u from sec$User u where u.group.id = :group')
        query.setParameter('group', group)
        users = dataManager.loadList(LoadContext.create(User).setQuery(query).setView('user.browse'))

        then: "ok"

        users.size() > 0

        cleanup:

        AppContext.setProperty('cuba.implicitConversionOfJpqlParams', null)
    }

    def "loadValues query parameter without implicit conversion - legacy behavior"() {
        def group = dataManager.load(LoadContext.create(Group).setId(TestSupport.COMPANY_GROUP_ID))

        ValueLoadContext.Query query

        AppContext.setProperty('cuba.implicitConversionOfJpqlParams', 'true')

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference object, no implicit conversion"

        query = ValueLoadContext.createQuery('select u.id, u.login from sec$User u where u.group = :group')
        query.setParameter('group', group, false) // no implicit conversion
        def list = dataManager.loadValues(ValueLoadContext.create().setQuery(query).addProperty('id').addProperty('login'))

        then: "ok"

        list.size() > 0

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference object, implicit conversion"

        query = ValueLoadContext.createQuery('select u.id, u.login from sec$User u where u.group = :group')
        query.setParameter('group', group)
        dataManager.loadValues(ValueLoadContext.create().setQuery(query).addProperty('id').addProperty('login'))

        then: "fail"

        thrown(IllegalArgumentException)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference id, implicit conversion"

        query = ValueLoadContext.createQuery('select u.id, u.login from sec$User u where u.group.id = :group')
        query.setParameter('group', group)
        list = dataManager.loadValues(ValueLoadContext.create().setQuery(query).addProperty('id').addProperty('login'))

        then: "ok"

        list.size() > 0

        cleanup:

        AppContext.setProperty('cuba.implicitConversionOfJpqlParams', null)
    }

    def "loadValues query parameter without implicit conversion"() {
        def group = dataManager.load(LoadContext.create(Group).setId(TestSupport.COMPANY_GROUP_ID))

        ValueLoadContext.Query query

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference object"

        query = ValueLoadContext.createQuery('select u.id, u.login from sec$User u where u.group = :group')
        query.setParameter('group', group)
        def list = dataManager.loadValues(ValueLoadContext.create().setQuery(query).addProperty('id').addProperty('login'))

        then: "ok"

        list.size() > 0

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "condition by reference id"

        query = ValueLoadContext.createQuery('select u.id, u.login from sec$User u where u.group.id = :groupId')
        query.setParameter('groupId', group.id)
        list = dataManager.loadValues(ValueLoadContext.create().setQuery(query).addProperty('id').addProperty('login'))

        then: "ok"

        list.size() > 0

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        when: "wrong condition by reference id"

        query = ValueLoadContext.createQuery('select u.id, u.login from sec$User u where u.group.id = :group')
        query.setParameter('group', group)
        list = dataManager.loadValues(ValueLoadContext.create().setQuery(query).addProperty('id').addProperty('login'))

        then: "fail"

        thrown(IllegalArgumentException)
    }

    def "load without query and id"() {

        when:

        User user = dataManager.load(LoadContext.create(User))

        then:

        noExceptionThrown()
        user != null

        when:

        List<User> users = dataManager.loadList(LoadContext.create(User))

        then:

        noExceptionThrown()
        !users.isEmpty()

        when:

        long count = dataManager.getCount(LoadContext.create(User))

        then:

        noExceptionThrown()
        count > 0
    }

    def "more than one parameter without implicit conversion #1163"() {

        when:

        LoadContext.Query query = LoadContext.createQuery('select e from test$Foo e where e.ref1 = :ref1 and e.ref2 = :ref2')
                .setParameter('ref1', 'val1', false)
                .setParameter('ref2', 'val2', false)
                .setParameter('ref3', 'val3', false)

        then:

        query.getNoConversionParams() == ['ref1', 'ref2', 'ref3'].toArray(new String[0])
    }

    def "load uses _base view by default"() {

        def product = new Product(name: 'p1', quantity: 100)
        def line = new OrderLine(product: product, quantity: 10)
        dataManager.commit(product, line)

        when:

        def line1 = dataManager.load(Id.of(line)).one()

        then:

        AppBeans.get(EntityStates).isLoadedWithView(line1, View.BASE)
        AppBeans.get(MetadataTools).getInstanceName(line1) == 'p1 10'

        cleanup:

        cont.deleteRecord(line, product)
    }
}
