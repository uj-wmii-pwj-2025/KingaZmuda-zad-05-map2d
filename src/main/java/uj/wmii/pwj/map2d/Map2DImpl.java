package uj.wmii.pwj.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Interface providing 2-dimensional map.
 * It can be viewed as rows and cells sheet, with row keys and column keys.
 * Row and Column keys are using standard comparison mechanisms.
 *
 * @param <R> type of row keys
 * @param <C> type column keys
 * @param <V> type of values
 */
public class Map2DImpl<R, C, V> implements Map2D<R, C, V> {

    private Map<R, Map<C, V>> map;
    private int size;

    public Map2DImpl() {
        this.map = new HashMap<R, Map<C, V>>();
        size = 0;
    }
    /**
     * Puts a value to the map, at given row and columns keys.
     * If specified row-column key already contains element, it should be replaced.
     *
     * @param rowKey row part of the key.
     * @param columnKey column part of the key.
     * @param value object to put. It can be null.
     * @return object, that was previously contained by map within these coordinates, or {@code null} if it was empty.
     * @throws NullPointerException if rowKey or columnKey are {@code null}.
     */
    @Override
    public V put(R rowKey, C columnKey, V value) {
        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("Row and column keys cannot be null");
        }

        Map<C, V> nestedMap = map.get(rowKey);

        if (nestedMap == null) {
            nestedMap = new HashMap<C, V>();
            map.put(rowKey, nestedMap);
        }

        boolean hadKey = nestedMap.containsKey(columnKey);
        V pastValue = nestedMap.put(columnKey, value);

        if (!hadKey) {
            size += 1;
        }

        return pastValue;
    }

    /**
     * Gets a value from the map from given key.
     *
     * @param rowKey row part of a key.
     * @param columnKey column part of a key.
     * @return object contained at specified key, or {@code null}, if the key does not contain an object.
     */
    @Override
    public V get(R rowKey, C columnKey) {
        Map<C, V> row = map.get(rowKey);
        if (row != null) {
            return row.get(columnKey);
        }
        return null;
    }

    /**
     * Gets a value from the map from given key. If specified value does not exist, returns {@code defaultValue}.
     *
     * @param rowKey row part of a key.
     * @param columnKey column part of a key.
     * @param defaultValue value to be returned, if specified key does not contain a value.
     * @return object contained at specified key, or {@code defaultValue}, if the key does not contain an object.
     */
    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        V value = get(rowKey, columnKey);
        if (value == null) {
            return defaultValue;
        }
        return value;
    };

    /**
     * Removes a value from the map from given key.
     *
     * @param rowKey row part of a key.
     * @param columnKey column part of a key.
     * @return object contained at specified key, or {@code null}, if the key didn't contain an object.
     */
    @Override
    public V remove(R rowKey, C columnKey) {
        V value = get(rowKey, columnKey);
        if (value != null) {
            Map<C, V> nestedMap = map.get(rowKey);
            size -= 1;
            return nestedMap.remove(columnKey);
        }
        return null;
    };

    /**
     * Checks if map contains no elements.
     * @return {@code true} if map doesn't contain any values; {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    };

    /**
     * Checks if map contains any element.
     * @return {@code true} if map contains at least one value; {@code false} otherwise.
     */
    @Override
    public boolean nonEmpty() {
        return size != 0;
    };

    /**
     * Return number of values being stored by this map.
     * @return number of values being stored
     */
    @Override
    public int size() {
        return size;

    };

    /**
     * Removes all objects from a map.
     */
    @Override
    public void clear() {
        map.clear();
        size = 0;
    }

    /**
     * Returns a view of mappings for specified key.
     * Result map should be immutable. Later changes to this map should not affect result map.
     *
     * @param rowKey row key to get view map for.
     * @return map with view of particular row. If there is no values associated with specified row, empty map is returned.
     */
    @Override
    public Map<C, V> rowView(R rowKey)  {
        Map<C, V> row = map.get(rowKey);

        if (row != null && !row.isEmpty()) {
            return Collections.unmodifiableMap(new HashMap<>(row));
        }

        return Collections.emptyMap();
    }

    /**
     * Returns a view of mappings for specified column.
     * Result map should be immutable. Later changes to this map should not affect returned map.
     *
     * @param columnKey column key to get view map for.
     * @return map with view of particular column. If there is no values associated with specified column, empty map is returned.
     */
    @Override
    public Map<R, V> columnView(C columnKey) {
        Map<C, Map<R, V>> columnView = columnMapView();
        Map<R, V> column = columnView.get(columnKey);

        if (column != null && !column.isEmpty()) {
            return Collections.unmodifiableMap(new HashMap<>(column));
        }

        return Collections.emptyMap();
    }

    /**
     * Checks if map contains specified value.
     * @param value value to be checked
     * @return {@code true} if map contains specified value; {@code false} otherwise.
     */
    @Override
    public boolean containsValue(V value) {
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            for (Map.Entry<C, V> nestedEntry : entry.getValue().entrySet()) {
                if (nestedEntry.getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if map contains a value under specified key.
     * @param rowKey row key to be checked
     * @param columnKey column key to be checked
     * @return {@code true} if map contains specified key; {@code false} otherwise.
     */
    @Override
    public boolean containsKey(R rowKey, C columnKey) {
        return get(rowKey, columnKey) != null;
    }

    /**
     * Checks if map contains at least one value within specified row.
     * @param rowKey row to be checked
     * @return {@code true} if map at least one value within specified row; {@code false} otherwise.
     */
    @Override
    public boolean containsRow(R rowKey) {
        return map.get(rowKey) != null;
    }

    /**
     * Checks if map contains at least one value within specified column.
     * @param columnKey column to be checked
     * @return {@code true} if map at least one value within specified column; {@code false} otherwise.
     */
    @Override
    public boolean containsColumn(C columnKey) {
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            if (get(entry.getKey(), columnKey) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a view of this map as map of rows to map of columns to values.
     * Result map should be immutable. Later changes to this map should not affect returned map.
     *
     * @return map with row-based view of this map. If this map is empty, empty map should be returned.
     */
    @Override
    public Map<R, Map<C,V>> rowMapView() {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<R, Map<C, V>> rowView = new HashMap<>();

        for (Map.Entry<R, Map<C, V>> e : this.map.entrySet()) {
            rowView.put(e.getKey(), Collections.unmodifiableMap(new HashMap<>(e.getValue())));
        }

        return Collections.unmodifiableMap(rowView);
    }

    /**
     * Return a view of this map as map of columns to map of rows to values.
     * Result map should be immutable. Later changes to this map should not affect returned map.
     *
     * @return map with column-based view of this map. If this map is empty, empty map should be returned.
     */
    @Override
    public Map<C, Map<R,V>> columnMapView() {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<C, Map<R, V>> columnView = new HashMap<C, Map<R, V>>();
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            for (Map.Entry<C, V> nestedEntry : entry.getValue().entrySet()) {
               C columnKey = nestedEntry.getKey();
               columnView.put(columnKey, new HashMap<>());
            }
        }

        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            Map<R, V> column = null;
            C columnKey = null;
            for (Map.Entry<C, V> nestedEntry : entry.getValue().entrySet()) {
                columnKey = nestedEntry.getKey();
                column = columnView.get(columnKey);
                R rowKey = entry.getKey();
                V value = nestedEntry.getValue();
                column.put(rowKey, value);
            }
            columnView.remove(columnKey);
            columnView.put(columnKey, column);
        }

        return columnView;
    }

    /**
     * Fills target map with all key-value maps from specified row.
     *
     * @param target map to be filled
     * @param rowKey row key to get data to fill map from
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        if (target == null) {
            return this;
        }

        if (map == null || map.isEmpty()) {
            return this;
        }

        Map<C, V> row = map.get(rowKey);
        if (row == null) {
            return this;
        }

        target.clear();
        target.putAll(row);
        return this;
    }

    /**
     * Fills target map with all key-value maps from specified row.
     *
     * @param target map to be filled
     * @param columnKey column key to get data to fill map from
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        if (target == null) {
            return this;
        }

        Map<C, Map<R, V>> columnView = columnMapView();
        if (columnView == null || columnView.isEmpty()) {
            return this;
        }

        Map<R, V> column = columnView.get(columnKey);
        if (column != null && !column.isEmpty()) {
            target.clear();
            target.putAll(column);
        }
        return this;
    }


    /**
     * Puts all content of {@code source} map to this map.
     *
     * @param source map to make a copy from
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        if (source != null) {
            for (Map.Entry<? extends R, ? extends Map<? extends C, ? extends V>> entry : source.rowMapView().entrySet()) {
                for (Map.Entry<? extends C, ? extends V> nestedEntry : entry.getValue().entrySet()) {
                    R rowKey = entry.getKey();
                    C columnKey = nestedEntry.getKey();
                    V value = nestedEntry.getValue();
                    this.put(rowKey, columnKey, value);
                }
            }
        }
        return this;
    }

    /**
     * Puts all content of {@code source} map to this map under specified row.
     * Ech key of {@code source} map becomes a column part of key.
     *
     * @param source map to make a copy from
     * @param rowKey object to use as row key
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V>  putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        if (source != null) {
            Map<C, V> pastRow = map.remove(rowKey);
            if (pastRow != null) {
                size -= pastRow.size();
                pastRow.putAll(source);
                map.put(rowKey, pastRow);
                size += pastRow.size();
            } else {
                Map<C, V> newRow = new HashMap<>();
                newRow.putAll(source);
                map.put(rowKey, newRow);
                size += newRow.size();
            }
        }

        return this;
    }

    /**
     * Puts all content of {@code source} map to this map under specified column.
     * Ech key of {@code source} map becomes a row part of key.
     *
     * @param source map to make a copy from
     * @param columnKey object to use as column key
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        if (source != null) {
            for (Map.Entry<? extends R, ? extends V> entry : source.entrySet())  {
                R rowKey = entry.getKey();
                V value = entry.getValue();
                Map<C, V> row = map.remove(rowKey);
                if (row != null) {
                    size -= row.size();
                    row.put(columnKey, value);
                    size += row.size();
                    map.put(rowKey, row);
                } else {
                    put(rowKey, columnKey, value);
                }
            }
        }
        return this;
    }

    /**
     * Creates a copy of this map, with application of conversions for rows, columns and values to specified types.
     * If as result of row or column conversion result key duplicates, then appropriate row and / or column in target map has to be merged.
     * If merge ends up in key duplication, then it's up to specific implementation which value from possible to choose.
     *
     * @param rowFunction function converting row part of key
     * @param columnFunction function converting column part of key
     * @param valueFunction function converting value
     * @param <R2> result map row key type
     * @param <C2> result map column key type
     * @param <V2> result map value type
     * @return new instance of {@code Map2D} with converted objects
     */
    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(
            Function<? super R, ? extends R2> rowFunction,
            Function<? super C, ? extends C2> columnFunction,
            Function<? super V, ? extends V2> valueFunction) {
        Map2D<R2, C2, V2> convertedMap = new Map2DImpl<>();

        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            for (Map.Entry<C, V> nestedEntry : entry.getValue().entrySet()) {
                R rowKey = entry.getKey();
                C columnKey = nestedEntry.getKey();
                V value = nestedEntry.getValue();
                R2 convertedRowKey = rowFunction.apply(rowKey);
                C2 convertedColumnKey = columnFunction.apply(columnKey);
                V2 convertedValue = valueFunction.apply(value);

                if (!convertedMap.containsKey(convertedRowKey, convertedColumnKey)) {
                    //leave only first value
                    convertedMap.put(convertedRowKey, convertedColumnKey, convertedValue);
                }
            }
        }

        return convertedMap;
    }
}
