/**
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2005 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.drools.asm.util.attrs;

import java.util.List;
import java.util.Map;

import org.drools.asm.Attribute;
import org.drools.asm.ClassReader;
import org.drools.asm.Label;
import org.drools.asm.attrs.StackMapFrame;
import org.drools.asm.attrs.StackMapTableAttribute;
import org.drools.asm.attrs.StackMapType;

/**
 * An {@link ASMifiable} {@link StackMapTableAttribute} sub class.
 * 
 * @author Eugene Kuleshov
 */
public class ASMStackMapTableAttribute extends StackMapTableAttribute
    implements
    ASMifiable,
    Traceable {
    /**
     * Length of the attribute used for comparison
     */
    private int len;

    public ASMStackMapTableAttribute() {
        super();
    }

    public ASMStackMapTableAttribute(final List frames,
                                     final int len) {
        super( frames );
        this.len = len;
    }

    protected Attribute read(final ClassReader cr,
                             final int off,
                             final int len,
                             final char[] buf,
                             final int codeOff,
                             final Label[] labels) {
        final StackMapTableAttribute attr = (StackMapTableAttribute) super.read( cr,
                                                                                 off,
                                                                                 len,
                                                                                 buf,
                                                                                 codeOff,
                                                                                 labels );

        return new ASMStackMapTableAttribute( attr.getFrames(),
                                              len );
    }

    public void asmify(final StringBuffer buf,
                       final String varName,
                       final Map labelNames) {
        final List frames = getFrames();
        if ( frames.size() == 0 ) {
            buf.append( "List frames = Collections.EMPTY_LIST;\n" );
        } else {
            buf.append( "List frames = new ArrayList();\n" );
            for ( int i = 0; i < frames.size(); i++ ) {
                buf.append( "{\n" );
                final StackMapFrame f = (StackMapFrame) frames.get( i );
                declareLabel( buf,
                              labelNames,
                              f.label );

                final String frameVar = varName + "frame" + i;
                asmifyTypeInfo( buf,
                                frameVar,
                                labelNames,
                                f.locals,
                                "locals" );
                asmifyTypeInfo( buf,
                                frameVar,
                                labelNames,
                                f.stack,
                                "stack" );

                buf.append( "StackMapFrame " ).append( frameVar ).append( " = new StackMapFrame(" ).append( labelNames.get( f.label ) ).append( ", locals, stack);\n" );
                buf.append( "frames.add(" ).append( frameVar ).append( ");\n" );
                buf.append( "}\n" );
            }
        }
        buf.append( "StackMapTableAttribute " ).append( varName );
        buf.append( " = new StackMapTableAttribute(frames);\n" );
    }

    void asmifyTypeInfo(final StringBuffer buf,
                        final String varName,
                        final Map labelNames,
                        final List infos,
                        final String field) {
        if ( infos.size() == 0 ) {
            buf.append( "List " ).append( field ).append( " = Collections.EMPTY_LIST;\n" );
        } else {
            buf.append( "List " ).append( field ).append( " = new ArrayList();\n" );
            buf.append( "{\n" );
            for ( int i = 0; i < infos.size(); i++ ) {
                final StackMapType typeInfo = (StackMapType) infos.get( i );
                final String localName = varName + "Info" + i;
                final int type = typeInfo.getType();
                buf.append( "StackMapType " ).append( localName ).append( " = StackMapType.getTypeInfo( StackMapType.ITEM_" ).append( StackMapType.ITEM_NAMES[type] ).append( ");\n" );

                switch ( type ) {
                    case StackMapType.ITEM_Object : //
                        buf.append( localName ).append( ".setObject(\"" ).append( typeInfo.getObject() ).append( "\");\n" );
                        break;

                    case StackMapType.ITEM_Uninitialized : //
                        declareLabel( buf,
                                      labelNames,
                                      typeInfo.getLabel() );
                        buf.append( localName ).append( ".setLabel(" ).append( labelNames.get( typeInfo.getLabel() ) ).append( ");\n" );
                        break;
                }
                buf.append( field ).append( ".add(" ).append( localName ).append( ");\n" );
            }
            buf.append( "}\n" );
        }
    }

    static void declareLabel(final StringBuffer buf,
                             final Map labelNames,
                             final Label l) {
        String name = (String) labelNames.get( l );
        if ( name == null ) {
            name = "l" + labelNames.size();
            labelNames.put( l,
                            name );
            buf.append( "Label " ).append( name ).append( " = new Label();\n" );
        }
    }

    public void trace(final StringBuffer buf,
                      final Map labelNames) {
        final List frames = getFrames();
        buf.append( "[\n" );
        for ( int i = 0; i < frames.size(); i++ ) {
            final StackMapFrame f = (StackMapFrame) frames.get( i );

            buf.append( "    Frame:" );
            appendLabel( buf,
                         labelNames,
                         f.label );

            buf.append( " locals[" );
            traceTypeInfo( buf,
                           labelNames,
                           f.locals );
            buf.append( "]" );
            buf.append( " stack[" );
            traceTypeInfo( buf,
                           labelNames,
                           f.stack );
            buf.append( "]\n" );
        }
        buf.append( "  ] length:" ).append( this.len ).append( "\n" );
    }

    private void traceTypeInfo(final StringBuffer buf,
                               final Map labelNames,
                               final List infos) {
        String sep = "";
        for ( int i = 0; i < infos.size(); i++ ) {
            final StackMapType t = (StackMapType) infos.get( i );

            buf.append( sep ).append( StackMapType.ITEM_NAMES[t.getType()] );
            sep = ", ";
            if ( t.getType() == StackMapType.ITEM_Object ) {
                buf.append( ":" ).append( t.getObject() );
            }
            if ( t.getType() == StackMapType.ITEM_Uninitialized ) {
                buf.append( ":" );
                appendLabel( buf,
                             labelNames,
                             t.getLabel() );
            }
        }
    }

    protected void appendLabel(final StringBuffer buf,
                               final Map labelNames,
                               final Label l) {
        String name = (String) labelNames.get( l );
        if ( name == null ) {
            name = "L" + labelNames.size();
            labelNames.put( l,
                            name );
        }
        buf.append( name );
    }

}
