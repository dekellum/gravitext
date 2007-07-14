package com.gravitext.htmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A factory and container of unique {@link Key} instances. The
 * following code sample shows the common usage pattern:
 *
 * <code><pre>
 * public class MyKeys
 * {
 *     public static final KeySpace KEY_SPACE = new KeySpace();
 *     
 *     public static final Key&lt;Double> DBLKEY = 
 *         KEY_SPACE.create( "DBLKEY", Double.class );
 * 
 *     public static final Key&lt;String> STRKEY = 
 *         KEY_SPACE.create( "STRKEY", String.class );
 * }
 * </pre></code>
 *
 * <p>KeySpace instances are internally synchronized to support
 * concurrent access interleaved with new key creation.  Copy-on-write
 * semantics are used, such that read operations are unsynchronized
 * while key creation is synchronized and relatively costly. This
 * trade-off is desirable given the usage pattern of key creation
 * commencing on system startup but generally completing shortly
 * thereafter, while read operations continue with high frequency.</p>
 * 
 * @author David Kellum
 */
public final class KeySpace
{ 
    /**
     * Create a new Key instance.
     * @param name a unique name. By convention, this is the String
     * equivalent of the constant to which the Key will be assigned.
     * @param valueType the runtime class type of values to be
     * associated with this key.
     * @throws IllegalArgumentException if a Key by the specified name
     * has already been created.
     */
    public synchronized <T> Key<T> create( String name, Class<T> valueType )
    {
        final State s = _state.get();
        
        if( s.keyNames.containsKey( name ) ) {
            throw new IllegalArgumentException( 
                "Invalid attempt to create a second key with name '" +
                name + "'." ); 
        }
            
        Key<T> key = new Key<T>( name, valueType, this, s.length );

        _state.set( s.add( key ) );

        return key;
    }

    /**
     * Get Key by name.
     * @return the key, or null if no such key by this name was
     * previously created.
     */
    public Key<?> get( String name )
    {
        return _state.get().keyNames.get( name );
    }

    /**
     * Get an existing key by name, or if no such key has yet been
     * created, create a new "anonymous" key with the specified name
     * and value type.  The Key is anonymous in that it will
     * typically not be assigned to a constant value by this method.
     * @return existing or new Key 
     */
    public synchronized Key<?> getOrCreate( String name, 
                                            Class<?> anonValueType )
    {
        final State s = _state.get();
        Key key = s.keyNames.get( name );
        if( key == null ) { 
            key = create( name, anonValueType );
        }
        return key;
    }
    
    /**
     * Return the current number of keys that have been created in this 
     * KeySpace.
     */
    public int size()
    {
        return _state.get().length;
    }

    /**
     * Return the list of Keys in this KeySpace in the order they were
     * created.  The index position of each key in the list will equal
     * the key.id().
     */
    List<Key> keySequence()
    {
        //FIXME: Make public and protect from writes?
        return _state.get().keySequence;
    }

    
    private final static class State
    {
        public static final State EMPTY = 
            new State( new ArrayList<Key>(), new HashMap<String,Key>() );
        
        public State add( Key key )
        {
            List<Key> seq = new ArrayList<Key>( length + 1 );
            seq.addAll( keySequence );
            seq.add( key );
            
            Map<String,Key> names = new HashMap<String,Key>( length + 1 );
            names.putAll( keyNames );
            names.put( key.name(), key );
            
            return new State( seq, names );                
        }
        
        private State( List<Key> seq, Map<String, Key> names )
        {
            length = seq.size();
            keySequence = seq;
            keyNames = names;
        }
        
        final int length;               
        final List<Key> keySequence;    //unmodified after construction
        final Map<String,Key> keyNames; //unmodified after construction
    }
    
    private final AtomicReference<State> _state = 
        new AtomicReference<State>( State.EMPTY );
 }
