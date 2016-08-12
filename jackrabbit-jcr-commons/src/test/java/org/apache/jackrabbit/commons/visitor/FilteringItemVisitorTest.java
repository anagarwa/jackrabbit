package org.apache.jackrabbit.commons.visitor;


//import org.apache.jackrabbit.commons.AbstractRepository;
//import org.apache.jackrabbit.commons.JcrUtils;
//import org.apache.jackrabbit.commons.MockCase;
//import org.apache.jackrabbit.commons.cnd.CndImporter;
//import org.apache.jackrabbit.test.AbstractJCRTest;
//
//import javax.jcr.*;
//import java.io.InputStreamReader;
//import java.io.Reader;

import org.apache.jackrabbit.commons.AbstractRepository;
import org.apache.jackrabbit.commons.MockCase;
import org.junit.Assert;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.test.AbstractJCRTest;

/**
 * Test cases for the {@link FilteringItemVisitorTest} class.
 */
public class FilteringItemVisitorTest

        extends AbstractJCRTest {


//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    public void testVisitor()throws RepositoryException {
//
//        getHelper().getRepository();
//
//
//    }

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
