/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.core.state;

import org.apache.jackrabbit.core.PropertyId;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * <code>NodeReferences</code> represents the references (i.e. properties of
 * type <code>REFERENCE</code>) to a particular node (denoted by its uuid).
 */
public class NodeReferences implements Serializable {

    /**
     * Serial UID
     */
    static final long serialVersionUID = 7007727035982680717L;

    /**
     * Logger instance
     */
    private static Logger log = Logger.getLogger(NodeReferences.class);

    /**
     * id of the target node
     */
    protected NodeReferencesId targetId;

    /**
     * list of PropertyId's (i.e. the id's of the properties that refer to
     * the target node denoted by <code>targetId</code>).
     * <p/>
     * note that the list can contain duplicate entries because a specific
     * REFERENCE property can contain multiple references (if it's multi-valued)
     * to potentially the same target node.
     */
    protected ArrayList references = new ArrayList();

    /**
     * Package private constructor
     *
     * @param targetId
     */
    public NodeReferences(NodeReferencesId targetId) {
        this.targetId  = targetId;
    }

    /**
     * Return the target id of this node references object.
     * @return target id
     */
    public NodeReferencesId getTargetId() {
        return targetId;
    }

    /**
     * Return the UUID of the target id
     * @return UUID of the target id
     */
    public String getUUID() {
        return targetId.getUUID();
    }

    /**
     * Return a flag indicating whether this object holds any references
     * @return <code>true</code> if this object holds references,
     * <code>false</code> otherwise
     */
    public boolean hasReferences() {
        return !references.isEmpty();
    }

    /**
     * @return
     */
    public List getReferences() {
        return Collections.unmodifiableList(references);
    }

    /**
     * @param refId
     */
    public void addReference(PropertyId refId) {
        references.add(refId);
    }

    /**
     * @param references
     */
    public void addAllReferences(List references) {
        references.addAll(references);
    }

    /**
     * @param refId
     * @return
     */
    public boolean removeReference(PropertyId refId) {
        return references.remove(refId);
    }

    /**
     *
     */
    public void clearAllReferences() {
        references.clear();
    }
}
