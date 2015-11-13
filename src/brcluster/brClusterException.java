//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// brCluster - The easy, generic and free data clustering library
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Copyright (C) 2005  Henrique Santos Camargos Rocha
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// For more information or contact, visit the project page on the sourceforge
// http://sourceforge.net/projects/brcluster/
//
// [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
// in the United States and other countries.]
//

/*
 * jClusterException.java
 *
 * Created on 28 de Maio de 2005, 13:12
 */

package brcluster;

/**
 *
 * @author  Henrique
 */
public class brClusterException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>jClusterException</code> without detail message.
     */
    public brClusterException() {
    }
    
    
    /**
     * Constructs an instance of <code>jClusterException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public brClusterException(String msg) {
        super(msg);
    }
}
