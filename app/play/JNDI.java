package play;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JNDI {

    private static final String IN_MEMORY_JNDI = "tyrex.naming.MemoryContextFactory";
    private static final String IN_MEMORY_URL = "/";
    private static InitialContext intialContext;

    static {
        java.util.Hashtable<String,String> env = new java.util.Hashtable<String,String>();
        String ctxFactory = Play.application().configuration().getString(Context.INITIAL_CONTEXT_FACTORY);
        String provUrl = Play.application().configuration().getString(Context.PROVIDER_URL);

        if (ctxFactory == null || ctxFactory.trim().isEmpty()) {
            ctxFactory = IN_MEMORY_JNDI;
        }
        if (provUrl == null || provUrl.trim().isEmpty()) {
            provUrl = IN_MEMORY_URL;
        }

        env.put(Context.INITIAL_CONTEXT_FACTORY, ctxFactory);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, ctxFactory);
        env.put(Context.PROVIDER_URL, provUrl);
        System.setProperty(Context.PROVIDER_URL, provUrl);

        try {
            intialContext = new InitialContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static InitialContext getContext() {
        return intialContext;
    }
}
