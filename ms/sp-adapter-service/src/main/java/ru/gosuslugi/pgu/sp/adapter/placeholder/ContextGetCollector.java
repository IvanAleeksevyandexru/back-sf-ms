package ru.gosuslugi.pgu.sp.adapter.placeholder;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Проксирует вызовы и накапливает ключи вызванных гет методов
 */
public class ContextGetCollector implements Context {

    /**
     * Специальный префикс, который velocity добавляет для вычитывания значения для нераспознанного плейсхолдера
     * См.  org.apache.velocity.runtime.parser.node.ASTReference#getNullString(InternalContextAdapter)}
     */
    public static final String LITERAL_PREFIX = ".literal.";

    private final Context context;

    private final List<String> usedKeys = new ArrayList<>();

    public ContextGetCollector(Context context) {
        this.context = context;
    }

    @Override
    public Object put(String key, Object value) {
        return this.context.put(key, value);
    }

    @Override
    public Object get(String key) {
        Object o = this.context.get(key);
        usedKeys.add(key);
        return o;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.context.containsKey(key);
    }

    @Override
    public Object[] getKeys() {
        return this.context.getKeys();
    }

    @Override
    public Object remove(Object key) {
        return this.context.remove(key);
    }

    public List<String> getUsedKeys() {
        return usedKeys;
    }

    /**
     * @return фильтруем список с префиксом LITERAL_PREFIX и убераем его. Получаем те плейсхолдеры, которые остались нераспознаны
     */
    public List<String> getLiteralKeys() {

        return usedKeys
            .stream()
            .filter(str -> str.startsWith(LITERAL_PREFIX))

              // https://jira.egovdev.ru/browse/EPGUCORE-47599 It is not a placeholder. It is default value for "$!" structure. For example: $!d7. Suppressing
            .filter(str -> !LITERAL_PREFIX.equals(str))
            .map(str -> str.substring(LITERAL_PREFIX.length()))
            .collect(Collectors.toList());
    }
}
