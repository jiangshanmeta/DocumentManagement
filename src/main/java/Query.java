import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Query implements Predicate<Document> {
    private final Map<String,String> clauses;

    public static Query parse(final String query ){
        Map<String,String> clauses = Arrays.stream(query.split(",")).map(str->str.split(":")).collect(Collectors.toMap((p)->p[0],p->p[1]));

        return new Query(clauses);
    }

    public Query(final Map<String,String> clauses){
        this.clauses = clauses;
    }


    @Override
    public boolean test(Document document) {
        return clauses.entrySet().stream().allMatch(entry->{
            final String documentValue = document.getAttribute(entry.getKey());
            final String queryValue = entry.getValue();
            return queryValue != null && documentValue != null && documentValue.contains(queryValue);
        });
    }
}
