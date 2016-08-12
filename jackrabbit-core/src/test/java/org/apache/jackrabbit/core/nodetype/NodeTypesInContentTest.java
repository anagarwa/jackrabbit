/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.core.nodetype;

import org.apache.jackrabbit.commons.visitor.FilteringItemVisitor;
import org.apache.jackrabbit.test.AbstractJCRTest;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.nodetype.xml.NodeTypeReader;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;

import javax.jcr.ItemVisitor;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.Node;
import javax.jcr.util.TraversingItemVisitor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

/**
 * <code>NodeTypesInContentTest</code>...
 */
public class NodeTypesInContentTest extends AbstractJCRTest {

    /**
     * custom node type defs defining non-string default values
     */
    private static final String TEST_NODETYPES = "org/apache/jackrabbit/core/nodetype/xml/test_nodetypes.xml";

    protected void setUp() throws Exception {
        isReadOnly = true;
        super.setUp();
        
        InputStream xml = getClass().getClassLoader().getResourceAsStream(TEST_NODETYPES);
        QNodeTypeDefinition[] ntDefs = NodeTypeReader.read(xml);
        NodeTypeRegistry ntReg = ((SessionImpl) superuser).getNodeTypeManager().getNodeTypeRegistry();
        if (!ntReg.isRegistered(ntDefs[0].getName())) {
            ntReg.registerNodeTypes(Arrays.asList(ntDefs));
        }
    }

    /**
     * Test for <a href="https://issues.apache.org/jira/browse/JCR-1964">JCR-1964</a>
     * 
     * @throws javax.jcr.RepositoryException If an exception occurs.
     */
    public void testDefaultValues() throws RepositoryException {
        ItemVisitor visitor = new TraversingItemVisitor.Default() {

            public void visit(Property property) throws RepositoryException {
                if (JcrConstants.JCR_DEFAULTVALUES.equals(property.getName())) {
                    int type = property.getType();
                    Value[] vs = property.getValues();
                    for (int i = 0; i < vs.length; i++) {
                        assertEquals("Property type must match the value(s) type", type, vs[i].getType());
                    }
                }
            }
        };

        Node start = (Node) superuser.getItem("/jcr:system/jcr:nodeTypes");
        visitor.visit(start);
    }

    public void testFilteringVisitor() throws RepositoryException {

        Node root = superuser.getRootNode();
        Node testNode = root.addNode("visitorTest");
       // List<String> paths = new ArrayList<String>();
      //  paths.add(testNode.getPath());
        testNode.setProperty("visitedNode", false);
        for (int i = 0; i < 100; i++) {
            Node firstChildNode = testNode.addNode("a"+i);
            firstChildNode.setProperty("visitedNode", false);

            Node secondChildNode = firstChildNode.addNode("b"+i);
            secondChildNode.setProperty("visitedNode", false);

            Node thirdChildNode = secondChildNode.addNode("c"+i);
            thirdChildNode.setProperty("visitedNode", false);

        }

        testNode.getSession().save();
        FilteringItemVisitor v = new FilteringItemVisitor() {

            @Override
            protected void entering(Property property, int level) throws RepositoryException {
            }

            @Override
            protected void entering(Node node, int level) throws RepositoryException {
                int currentDepth = Thread.currentThread().getStackTrace().length;
                if (currentDepth > 200){
                    Assert.fail("Stack depth is more than 200");
                }

            }

            @Override
            protected void leaving(Property property, int level) throws RepositoryException {

            }

            @Override
            protected void leaving(Node node, int level) throws RepositoryException {
                node.setProperty("visitedNode", true);

            }
        };

        v.setBreadthFirst(true);
        testNode.accept(v);
        testNode.getSession().save();


        if ( !isVisited(testNode)) {
            Assert.fail("nodes not visted");

        }

        for (int i = 0; i < 100; i++) {
            Node firstChildNode = testNode.getNode("a" + i);
            if (!isVisited(firstChildNode)) {
                Assert.fail("nodes not visted");

            }

            Node secondChildNode = firstChildNode.getNode("b" + i);
            if (!isVisited(secondChildNode)) {
                Assert.fail("nodes not visted");

            }

            Node thirdChildNode = secondChildNode.getNode("c"+i);
            if (!isVisited(thirdChildNode)) {
                Assert.fail("nodes not visted");

            }

        }







    }

    public boolean isVisited(Node node) throws RepositoryException{
        return node.getProperty("visitedNode").getBoolean();
    }
}