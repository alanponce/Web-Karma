/*******************************************************************************
 * Copyright 2012 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/

package edu.isi.karma.modeling.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import edu.isi.karma.modeling.alignment.GraphUtil;
import edu.isi.karma.modeling.alignment.SteinerTree;
import edu.isi.karma.rep.alignment.Link;
import edu.isi.karma.rep.alignment.Node;


public class Algorithm {

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

	public static List<MatchedSubGraphs> computeMatchedSubGraphs(
			DirectedWeightedMultigraph<Node, Link> g1, 
			DirectedWeightedMultigraph<Node, Link> g2) {
		
		List<MatchedSubGraphs> matchedSubGraphs = 
				new ArrayList<MatchedSubGraphs>();
		
		List<Node> attributes1 = Util.getAttributes(g1);
		List<Node> attributes2 = Util.getAttributes(g2);
		
		if (attributes1.size() != attributes2.size())
		{
			System.out.println("Something went wrong, size of the attribute list of the two input graphs is not the same.");
			return matchedSubGraphs;
		}

		int attCount = attributes1.size(); // = attributes2.size();
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i = 0; i < attCount; i++)
			numbers.add(i);
		
		Set<Set<Integer>> powerSet = powerSet(new HashSet<Integer>(numbers));
		

		for (int k = 2; k < attCount - 1; k ++) {
			for (Set<Integer> set : powerSet) {
				if (set.size() != k)
					continue;
				
				List<Node> steinerNodes1 = new ArrayList<Node>();
				List<Node> steinerNodes2 = new ArrayList<Node>();
				for (Integer j : set) {
					steinerNodes1.add(attributes1.get(j));
					steinerNodes2.add(attributes2.get(j));
				}
				
				DirectedWeightedMultigraph<Node, Link> steinerTree1 = 
						computeSteinerTree(g1, steinerNodes1);
				DirectedWeightedMultigraph<Node, Link> steinerTree2 = 
						computeSteinerTree(g2, steinerNodes2);
				
				MatchedSubGraphs match = null;
				if (steinerTree1 != null && steinerTree2 != null) {
					match = new MatchedSubGraphs(steinerTree1, steinerTree2);
					matchedSubGraphs.add(match);
				}
			}
		}
		
		return matchedSubGraphs;
	}
	
	private static DirectedWeightedMultigraph<Node, Link> computeSteinerTree(
			DirectedWeightedMultigraph<Node, Link> graph,
			List<Node> steinerNodes) {
		
		SteinerTree st = new SteinerTree(new AsUndirectedGraph<Node, Link>(graph), 
				steinerNodes);
		
		DirectedWeightedMultigraph<Node, Link> tree = 
				(DirectedWeightedMultigraph<Node, Link>)GraphUtil.asDirectedGraph(st.getSteinerTree());
		
		return tree;
	}
	
	
	public static void main(String[] args) {
//		List<Integer>
		Integer[] list = {1, 3, 5, 7};
		Set<Integer> set = new HashSet<Integer>(Arrays.asList(list));
		Set<Set<Integer>> powerSet = powerSet(set);
		
		for (Set<Integer> s : powerSet) {
			for (Integer i : s) {
				System.out.print(i);
				System.out.print(", ");
			}
			System.out.println();
		}
	}
}