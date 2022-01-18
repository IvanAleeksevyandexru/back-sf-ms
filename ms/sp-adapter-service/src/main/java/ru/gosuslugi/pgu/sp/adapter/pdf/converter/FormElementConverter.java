package ru.gosuslugi.pgu.sp.adapter.pdf.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.List;
import java.util.Map;

/**
 * Legacy SF converter
 * Used for translating value map to xml for proper EPGU's xslt transfomation
 * TODO further refactoring is required
 */
public class FormElementConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map) source;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                writer.startNode(entry.getKey());
                if (entry.getValue() instanceof Map || entry.getValue() instanceof List) {
                    marshal(entry.getValue(), writer, context);
                } else if (entry.getValue() != null) {
                    writer.setValue(String.valueOf(entry.getValue()));
                }

                writer.endNode();
            }
        } else if (source instanceof List) {
            List list = (List) source;
            if (!list.isEmpty()) {
                for (Object o : list) {
                    writer.startNode("item");
                    marshal(o, writer, context);
                    writer.endNode();
                }
            }
        }
    }

    @Override
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        throw new UnsupportedOperationException();
    }
}
