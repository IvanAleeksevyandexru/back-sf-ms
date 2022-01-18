package ru.gosuslugi.pgu.sp.adapter.pdf.driver;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Writer;

/**
 * Legacy SF requirements
 * TODO further refactoring is required
 */
public class XStreamDriver extends XppDriver {
    public HierarchicalStreamWriter createWriter(Writer out) {
        return new CompactWriter(out) {
            boolean cdata = false;

            public void startNode(String name, Class clazz) {
                super.startNode(name, clazz);
                cdata = (name.equals("content") || name.equals("name"));
            }

            protected void writeText(QuickWriter writer, String text) {
                if (cdata) {
                    writer.write("<![CDATA[");
                    writer.write(text);
                    writer.write("]]>");
                }

                writer.write(text);
            }
        };
    }
}
