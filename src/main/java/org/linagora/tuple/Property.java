package org.linagora.tuple;

import com.google.common.base.Objects;

public class Property {

    private final String namespace;
    private final String localName;
    private final String value;

    public Property(String namespace, String localName, String value) {
        this.namespace = namespace;
        this.localName = localName;
        this.value = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getLocalName() {
        return localName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(namespace, localName, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Property) {
            Property that = (Property) obj;
            return Objects.equal(this.namespace, that.namespace)
                && Objects.equal(this.localName, that.localName)
                && Objects.equal(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("namespace", namespace)
                .add("localName", localName)
                .add("value", value)
                .toString();
    }
}
