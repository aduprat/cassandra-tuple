package org.linagora.tuple;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.CQLDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.linagora.tuple.Property;
import org.linagora.tuple.Table;

import com.google.common.collect.ImmutableList;

public class TableTest {

    @Rule public CassandraCQLUnit cassandraCQLUnit = new CassandraCQLUnit(new CQLDataSet() {
        
        @Override
        public boolean isKeyspaceDeletion() {
            return true;
        }
        
        @Override
        public boolean isKeyspaceCreation() {
            return true;
        }
        
        @Override
        public String getKeyspaceName() {
            return "test";
        }
        
        @Override
        public List<String> getCQLStatements() {
            return ImmutableList.of();
        }
    });
    
    private Table testee;
    
    @Before
    public void setup() {
        testee = new Table(cassandraCQLUnit.session);
    }
    
    @Test
    public void getShouldReturnEmptyListWhenNoMatch() {
        List<Property> properties = testee.get(1);
        assertThat(properties).isEmpty();
    }
    
    @Test
    public void saveEmptyList() {
        testee.save(1, ImmutableList.of());
        List<Property> properties = testee.get(1);
        assertThat(properties).isEmpty();
    }
    
    @Test
    public void saveASingleton() {
        ImmutableList<Property> expectedProperties = ImmutableList.of(new Property("ns1", "key1", "value1"));
        testee.save(1, expectedProperties);
        List<Property> properties = testee.get(1);
        assertThat(properties).isEqualTo(expectedProperties);
    }
    
    @Test
    public void saveMultipleProperties() {
        ImmutableList<Property> expectedProperties = ImmutableList.of(
                new Property("ns1", "key1", "value1"),
                new Property("ns1", "key2", "value2"),
                new Property("ns2", "key1", "value3"));
        testee.save(1, expectedProperties);
        List<Property> properties = testee.get(1);
        assertThat(properties).isEqualTo(expectedProperties);
    }
}
