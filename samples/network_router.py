import heapq
from collections import defaultdict

class NetworkRouter:
    def __init__(self):
        self.graph = defaultdict(list)

    def add_link(self, source, target, cost):
        self.graph[source].append((target, cost))
        self.graph[target].append((source, cost))

    def shortest_path(self, start, goal):
        distances = {start: 0}
        parents = {start: None}
        queue = [(0, start)]
        while queue:
            cost, node = heapq.heappop(queue)
            if node == goal:
                break
            if cost > distances.get(node, float("inf")):
                continue
            for neighbor, edge_cost in self.graph[node]:
                new_cost = cost + edge_cost
                if new_cost < distances.get(neighbor, float("inf")):
                    distances[neighbor] = new_cost
                    parents[neighbor] = node
                    heapq.heappush(queue, (new_cost, neighbor))
        if goal not in parents:
            return [], None
        path = []
        current = goal
        while current is not None:
            path.append(current)
            current = parents[current]
        path.reverse()
        return path, distances[goal]

def main():
    router = NetworkRouter()
    links = [("A", "B", 4), ("A", "C", 2), ("C", "B", 1), ("B", "D", 5), ("C", "D", 8), ("D", "E", 3), ("C", "E", 10)]
    for source, target, cost in links:
        router.add_link(source, target, cost)
    path, cost = router.shortest_path("A", "E")
    print("Shortest path:", " -> ".join(path))
    print("Total cost:", cost)

if __name__ == "__main__":
    main()


