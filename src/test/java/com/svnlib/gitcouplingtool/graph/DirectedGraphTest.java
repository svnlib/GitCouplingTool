package com.svnlib.gitcouplingtool.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectedGraphTest {

    @Test
    void test() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        for (int i = 0; i < 10; i++) {
            graph.addVertex(i);
        }

        // Create edge
        final DirectedEdge<Integer> edge1 = graph.findOrCreateEdge(0, 1, 1);
        Assertions.assertNotNull(edge1);
        Assertions.assertEquals(0, (int) edge1.src);
        Assertions.assertEquals(1, (int) edge1.dest);
        Assertions.assertEquals(1, edge1.weight);

        // Find the previous edge
        final DirectedEdge<Integer> edge2 = graph.findOrCreateEdge(0, 1, 2);
        Assertions.assertNotNull(edge2);
        Assertions.assertEquals(0, (int) edge2.src);
        Assertions.assertEquals(1, (int) edge2.dest);
        Assertions.assertEquals(1, edge2.weight);
        Assertions.assertSame(edge1, edge2);

        // Create new edge if dest and src are changed
        final DirectedEdge<Integer> edge3 = graph.findOrCreateEdge(1, 0, 3);
        Assertions.assertNotNull(edge3);
        Assertions.assertEquals(1, (int) edge3.src);
        Assertions.assertEquals(0, (int) edge3.dest);
        Assertions.assertEquals(3, edge3.weight);
        Assertions.assertNotSame(edge1, edge3);
    }

}