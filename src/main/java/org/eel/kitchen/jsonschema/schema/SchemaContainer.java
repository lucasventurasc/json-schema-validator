/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;

import java.net.URI;

public final class SchemaContainer
{
    private static final URI EMPTY_LOCATOR = URI.create("#");

    private final JsonNode schema;
    private final JsonRef locator;

    public SchemaContainer(final JsonNode schema)
        throws JsonSchemaException
    {
        locator = JsonRef.fromNode(schema, "id");
        this.schema = cleanup(schema);

        checkLocator();
    }

    public SchemaContainer(final URI uri, final JsonNode node)
    {
        schema = cleanup(node);
        locator = new JsonRef(uri);
    }

    public static SchemaContainer anonymousSchema(final JsonNode node)
    {
        return new SchemaContainer(EMPTY_LOCATOR, cleanup(node));
    }

    public JsonRef getLocator()
    {
        return locator;
    }

    public JsonNode getSchema()
    {
        return schema;
    }

    public boolean contains(final JsonRef ref)
    {
        final JsonRef tmp = locator.resolve(ref);

        return locator.getRootAsURI().equals(tmp.getRootAsURI());
    }

    @Override
    public boolean equals(final Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (getClass() != o.getClass())
            return false;

        final SchemaContainer that = (SchemaContainer) o;

        return locator.equals(that.locator)
            && schema.equals(that.schema);
    }

    @Override
    public int hashCode()
    {
        return 31 * locator.hashCode() + schema.hashCode();
    }

    private static JsonNode cleanup(final JsonNode schema)
    {
        if (!schema.has("id"))
            return schema;

        final ObjectNode ret = schema.deepCopy();

        ret.remove("id");
        return ret;
    }

    private void checkLocator()
        throws JsonSchemaException
    {
        if (!locator.isAbsolute() && !locator.isEmpty())
            throw new JsonSchemaException("a parent schema's id must be "
                + "absolute");
    }
}
