package com.gravitext.xml.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gravitext.htmap.ArrayHTMap;
import com.gravitext.htmap.HTAccess;
import com.gravitext.htmap.Key;
import com.gravitext.htmap.KeySpace;
import com.gravitext.xml.producer.Namespace;

/**
 *
 * FIXME: Start with just a single non-polymorphic representation of
 * Document/Element, content, etc. May choose to divide this into parent and
 * child classes if it gets unwieldy.
 */
public final class Node
    implements HTAccess
{
    public static final KeySpace KEY_SPACE = new KeySpace();

    private static final KeySpace COMPAT_KEY_SPACE = new KeySpace();
    public  static final Key<CharSequence> CONTENT =
        COMPAT_KEY_SPACE.create( "content", CharSequence.class );

    public static Node newElement( String name )
    {
        return newElement( name, null );
    }

    public static Node newElement( String name, Namespace ns )
    {
        return new Node( null, name, ns );
    }

    public static Node newContent( CharSequence chars )
    {
        return new Node( chars, null, null );
    }

    public List<Node> children()
    {
        return _children;
    }

    public void addChild( Node node )
    {
        if( _children == EMPTY_CHILDREN ) {
            _children = new ArrayList<Node>(3);
        }

        node.detach();
        _children.add( node );
        node.setParent( this );
    }

    public void insertChild( int index, Node node )
    {
        if( _children == EMPTY_CHILDREN ) {
            _children = new ArrayList<Node>(3);
        }

        node.detach();
        _children.add( index, node );
        node.setParent( this );
    }

    public void detach()
    {
        if( _parent != null ) {
            _parent.removeChild( this );
            _parent = null;
        }
    }

    public CharSequence characters()
    {
        return _chars;
    }

    public void setCharacters( CharSequence chars )
    {
        _chars = chars;
    }

    public String name()
    {
        return _elementName;
    }

    public Namespace namespace()
    {
        return _namespace;
    }

    public void setElement( String name )
    {
        //FIXME: Asymmetric with above name().
        setElement( name, null );
    }

    public void setElement( String name, Namespace ns )
    {
        _elementName = name;
        _namespace = ns;
    }

    public List<AttributeValue> attributes()
    {
        return _attributes;
    }

    public void addAttribute( AttributeValue avalue )
    {
        if( _attributes == EMPTY_ATTS ) {
            _attributes = new ArrayList<AttributeValue>(3);
        }

        _attributes.add( avalue );
    }

    public void setAttributes( List<AttributeValue> attributes )
    {
        _attributes = attributes;
    }

    public void addNamespace( Namespace ns )
    {
        if( _spaces == EMPTY_NAMESPACES ) {
            _spaces = new ArrayList<Namespace>(3);
        }

        _spaces.add( ns );
    }

    public List<Namespace> namespaceDeclarations()
    {
        return _spaces;
    }

    public Node parent()
    {
        return _parent;
    }

    public boolean isElement()
    {
        return ( _elementName != null );
    }

    public <T, V extends T> T set( Key<T> key, V value )
    {
        if( key == CONTENT ) {
            CharSequence old = characters();
            setCharacters( CONTENT.valueType().cast( value ) );
            return key.valueType().cast( old );
        }

        if( _props == EMPTY_PROPS ) {
            _props = new ArrayHTMap( KEY_SPACE );
        }

        if( value != null ) {
            return set( key, value );
        }
        return remove( key );
    }

    public <T> T get( Key<T> key )
    {
        if( key == CONTENT ) {
            return key.valueType().cast( characters() );
        }
        return _props.get( key );
    }

    public <T> T remove( Key<T> key )
    {
        if( key == CONTENT ) {
            CharSequence old = characters();
            setCharacters("");
            return key.valueType().cast( old );
        }
        return _props.remove( key );
    }

    private Node( CharSequence chars, String elementName, Namespace ns )
    {
        _chars = chars;
        _elementName = elementName;
        _namespace = ns;
    }

    private void setParent( Node node )
    {
        _parent = node;
    }

    private void removeChild( Node node )
    {
        _children.remove( node );
    }

    private static final List<AttributeValue> EMPTY_ATTS =
        Collections.emptyList();
    private static final List<Namespace> EMPTY_NAMESPACES =
        Collections.emptyList();
    private static final List<Node> EMPTY_CHILDREN = Collections.emptyList();
    private static final ArrayHTMap EMPTY_PROPS = new ArrayHTMap( KEY_SPACE );

    private String _elementName = null;
    private Namespace _namespace = null;
    private List<AttributeValue> _attributes = EMPTY_ATTS;
    private List<Namespace> _spaces = EMPTY_NAMESPACES;
    private List<Node> _children = EMPTY_CHILDREN;

    private CharSequence _chars = null;

    private ArrayHTMap _props = EMPTY_PROPS;
    private Node _parent = null;

}
