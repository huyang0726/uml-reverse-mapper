package com.nitorcreations;

import static com.nitorcreations.DomainMapper.DEFAULTS;
import static com.nitorcreations.DomainMapper.DOMAIN_DECLARATION;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.nitorcreations.domain.Selfie;
import com.nitorcreations.domain.Task;
import com.nitorcreations.domain.Timesheet;
import com.nitorcreations.domain.person.DoubleReferer;
import com.nitorcreations.domain.person.Employee;
import com.nitorcreations.domain.person.Manager;
import com.nitorcreations.domain.person.Person;

public class DomainMapperTest {
    private DomainMapper domainMapper;

    @Test
    public void testDescribeDomain_singleClass() throws ClassNotFoundException {
        List<Class<?>> list = new ArrayList<>();
        list.add(Employee.class);
        domainMapper = new DomainMapper(list);
        String description = DOMAIN_DECLARATION + DEFAULTS + "\n  subgraph cluster_0 {\n    label = \"com.nitorcreations.domain.person\";\n    Employee\n  }\n}";
        assertThat(domainMapper.describeDomain(), is(description));
    }

    @Test
    public void testDescribeDomain_selfie() throws ClassNotFoundException {
        List<Class<?>> list = new ArrayList<>();
        list.add(Selfie.class);
        domainMapper = new DomainMapper(list);
        String description = DOMAIN_DECLARATION + DEFAULTS + "\n  subgraph cluster_0 {\n    label = \"com.nitorcreations.domain\";\n    Selfie\n  }\n  Selfie -> Selfie [ taillabel = \"me\" dir=forward arrowhead=open];\n}";
        assertThat(domainMapper.describeDomain(), is(description));
    }

    @Test
    public void testDescribeDomain_doubleReferer() throws ClassNotFoundException {
        domainMapper = new DomainMapper(Arrays.asList(DoubleReferer.class, Manager.class));
        String description = DOMAIN_DECLARATION + DEFAULTS + "\n  subgraph cluster_0 {\n    label = \"com.nitorcreations.domain.person\";\n    DoubleReferer\n    Manager\n  }\n  DoubleReferer -> Manager [ taillabel = \"myBoss\" dir=forward arrowhead=open];\n  DoubleReferer -> Manager [ taillabel = \"myTeamManager\" dir=forward arrowhead=open];\n}";
        assertThat(domainMapper.describeDomain(), is(description));
    }

    @Test
    public void testDescribeDomain_simpleInheritance() throws ClassNotFoundException {
        domainMapper = new DomainMapper(Arrays.<Class<?>> asList(Manager.class, Person.class));
        String description = DOMAIN_DECLARATION + DEFAULTS + "\n  subgraph cluster_0 {\n    label = \"com.nitorcreations.domain.person\";\n    Manager\n    Person\n  }\n  Manager -> Person [arrowhead=empty color=slategray];\n}";
        assertThat(domainMapper.describeDomain(), is(description));
    }

    @Test
    public void testDescribeDomain() throws ClassNotFoundException {
        domainMapper = new DomainMapper(Arrays.asList(Employee.class, Task.class, Manager.class, Timesheet.class, Person.class));
        String description = DOMAIN_DECLARATION + DEFAULTS + "\n  subgraph cluster_0 {\n    label = \"com.nitorcreations.domain.person\";\n    Employee\n    Manager\n    Person\n  }\n  subgraph cluster_1 {\n    label = \"com.nitorcreations.domain\";\n    Task\n    Timesheet\n  }\n  Task -> Employee [ taillabel = \"assignedEmployees\" dir=forward arrowhead=open];\n  Task -> Manager [ taillabel = \"manager\" dir=forward arrowhead=open];\n  Timesheet -> Employee [ taillabel = \"who\" dir=forward arrowhead=open];\n  Timesheet -> Task [ taillabel = \"task\" dir=forward arrowhead=open];\n  Employee -> Person [arrowhead=empty color=slategray];\n  Manager -> Person [arrowhead=empty color=slategray];\n}";
        assertThat(domainMapper.describeDomain(), is(description));
    }
}