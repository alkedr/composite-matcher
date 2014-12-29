package alkedr.matchers.reporting.matchers.map;

import alkedr.matchers.reporting.matchers.ValueExtractingMatcher;
import alkedr.matchers.reporting.matchers.ValuesExtractor;
import alkedr.matchers.reporting.matchers.map.extractors.MapAllValuesExtractor;
import alkedr.matchers.reporting.matchers.map.extractors.MapSizeExtractor;
import alkedr.matchers.reporting.matchers.map.extractors.MapValueExtractor;
import org.hamcrest.Matcher;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;

public class MapMatcher<T, U> extends ValueExtractingMatcher<Map<T, U>> {

    public EntryCheckAdder valueOf(T key) {
        return new EntryCheckAdder(new MapValueExtractor<T, U>(key));
    }


    public MapMatcher<T, U> hasSize(Integer value) {
        return hasSize(equalTo(value));
    }

    public MapMatcher<T, U> hasSize(Matcher<Integer> matcher) {
        addPlannedCheck(new MapSizeExtractor<T, U>("<size>"), asList(matcher));
        return this;
    }


    public MapMatcher<T, U> allValues(Matcher<? super U> matcher) {
        addPlannedCheck(new MapAllValuesExtractor<T, U>(), asList(matcher));
        return this;
    }


    public class EntryCheckAdder {
        private final ValuesExtractor<Map<T, U>> extractor;

        private EntryCheckAdder(ValuesExtractor<Map<T, U>> extractor) {
            this.extractor = extractor;
        }

        public MapMatcher<T, U> isEqualTo(U value) {
            return is(equalTo(value));
        }

        public MapMatcher<T, U> is(Matcher<? super U> valueMatcher) {
            addPlannedCheck(extractor, asList(valueMatcher));
            return MapMatcher.this;
        }
    }


/*
    public SubmapCheckAdder submapWithKeys(Matcher<? super T> keysMatcher) {
    }

    public SubmapCheckAdder submapWithValues(Matcher<? super U> valuesMatcher) {
    }

    public SubmapCheckAdder submapWithEntries(Matcher<? super Map.Entry<T, U>> keysMatcher) {
    }


    public EntryCheckAdder has() {
        return has(1);
    }

    private EntryCheckAdder has(int count) {
        return has(equalTo(count));
    }

    private EntryCheckAdder has(Matcher<? super Integer> countMatcher) {
        return new EntryCheckAdder(countMatcher);
    }



    public class EntryCheckAdder {
        private final Matcher<? super Integer> countMatcher;

        public EntryCheckAdder(Matcher<? super Integer> countMatcher) {
            this.countMatcher = countMatcher;
        }

        public MapMatcher<T, U> key(Matcher<? super T> keyMatcher) {
        }

        public MapMatcher<T, U> value(Matcher<? super U> valueMatcher) {
        }

        public MapMatcher<T, U> entry(Matcher<? super T> keyMatcher, Matcher<? super T> valueMatcher) {
        }

        public MapMatcher<T, U> entry(Matcher<? super Map.Entry<T, U>> entryMatcher) {
        }


        public MapMatcher<T, U> keys(Matcher<? super T> keyMatcher) {
            return key(keyMatcher);
        }

        public MapMatcher<T, U> values(Matcher<? super U> valueMatcher) {
            return value(valueMatcher);
        }

        public MapMatcher<T, U> entries(Matcher<? super T> keyMatcher, Matcher<? super T> valueMatcher) {
            return entry(keyMatcher, valueMatcher);
        }

        public MapMatcher<T, U> entries(Matcher<? super Map.Entry<T, U>> entryMatcher) {
            return entry(entryMatcher);
        }
    }
*/
}
