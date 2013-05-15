package play.db.jpa;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Wraps an action in am JPA transaction.
 */
public class TransactionalAction extends Action<Transactional> {
    
    public Result call(final Context ctx) throws Throwable {
        return JPA.withTransaction(
                configuration.value(),
                configuration.readOnly(),
                new play.libs.F.Function0<Result>() {
                    public Result apply() throws Throwable {
                        return delegate.call(ctx);
                    }
                }
        );
    }
    
}
