package org.linagora.tuple;

import static org.linagora.tuple.Table.TableDefinition.ID;
import static org.linagora.tuple.Table.TableDefinition.PROPERTIES;
import static org.linagora.tuple.Table.TableDefinition.PROPERTY_TYPE;
import static org.linagora.tuple.Table.TableDefinition.TABLE_NAME;

import java.util.List;
import java.util.stream.Collectors;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.google.common.collect.ImmutableList;

public class Table {

    private final Session session;

    public Table(Session session) {
        this.session = session;
        createTableIfNotExists(session);
    }

    private void createTableIfNotExists(Session session) {
        Create tableCreation = SchemaBuilder.createTable(TABLE_NAME)
            .ifNotExists()
            .addPartitionKey(ID, DataType.cint())
            .addColumn(PROPERTIES, DataType.list(PROPERTY_TYPE));
        session.execute(tableCreation);
    }
    
    public void save(int id, List<Property> properties) {
        Insert query = QueryBuilder.insertInto(TABLE_NAME)
                .value(ID, id)
                .value(PROPERTIES, properties.stream()
                        .map(x -> PROPERTY_TYPE.newValue(x.getNamespace(), x.getLocalName(), x.getValue()))
                        .collect(Collectors.toList()));
        session.execute(query);
    }
    
    public List<Property> get(int id) {
        Where query = QueryBuilder.select(PROPERTIES).from(TABLE_NAME)
            .where(QueryBuilder.eq(ID, id));
        
        ResultSet result = session.execute(query);
        if (result.isExhausted()) {
            return ImmutableList.of();
        }
        
        return result.one().getList(PROPERTIES, TupleValue.class).stream()
            .map(x -> new Property(x.getString(0), x.getString(1), x.getString(2)))
            .collect(Collectors.toList());
    }
    
    public interface TableDefinition {
        
        public static final String TABLE_NAME = "test";
        public static final TupleType PROPERTY_TYPE = TupleType.of(DataType.text(), DataType.text(), DataType.text());
        
        public static final String ID = "id";
        public static final String PROPERTIES = "properties";
        
        public static final List<String> FIELDS = ImmutableList.of(ID, PROPERTIES);
    }
}
