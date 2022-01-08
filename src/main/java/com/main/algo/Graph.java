package com.main.algo;

import com.main.controller.ControllerClass;
import com.main.model.Friendship;
import com.main.model.User;

import java.util.ArrayList;

public class Graph {
    int V;
    int max=0;
    ArrayList<Long> userIDs = new ArrayList<>();
    ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
    ArrayList<ArrayList<Long>> communities = new ArrayList<>();

    /**
     * create a Graph of friendships using a given controller
     * @param controller a FriendshipService
     */
    public Graph(ControllerClass controller){
        V = controller.getUserService().size();
        for(User user : controller.getUserService().getAllEntities()){
            userIDs.add(user.getId());
            adj.add(new ArrayList<>());
        }
        for(Friendship fr : controller.getFriendshipService().getAllEntities()){
            this.addEdge(fr.getId().getLeft(), fr.getId().getRight());
        }
    }

    /**
     * create an edge showing a friendship between src and dest
     * @param src ID of first user
     * @param dest ID of second user
     */
    void addEdge(Long src, Long dest){
        adj.get(userIDs.lastIndexOf(src)).add(userIDs.lastIndexOf(dest));
        adj.get(userIDs.lastIndexOf(dest)).add(userIDs.lastIndexOf(src));
    }
    void DFS(int v, boolean[] visited, ArrayList<Long> community){
        visited[v] = true;
        community.add(userIDs.get(v));
        for (int x : adj.get(v)) {
            if (!visited[x])
                DFS(x, visited, community);
        }
    }

    /**
     * generate the Graph
     */
    public void runConnectedComponents(){
        boolean[] visited = new boolean[V];
        for (int v = 0; v < V; ++v) {
            if (!visited[v]) {
                communities.add(new ArrayList<>());
                int lastIndex = communities.size() - 1;
                DFS(v, visited,communities.get(lastIndex));
                int size = communities.get(lastIndex).size();
                if(size > max)
                    max = size;
            }
        }
    }

    /**
     *
     * @return an ArrayList of communities
     */
    public ArrayList<ArrayList<Long>> getCommunities(){
        return communities;
    }

    /**
     *
     * @return the size of biggest community
     */
    public int maxSize(){
        return max;
    }
}
