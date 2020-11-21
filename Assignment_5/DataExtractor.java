import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DataExtractor extends Observable {

    private int[][] points;
    private int[][] normalizedPoints;
    private int fileLength = 0;
    private List<Thread> threadList = new ArrayList<>();
    private List<List<Integer>> routeList = new ArrayList<>();
    private List<List<Integer>> costList = new ArrayList<>();

    public void extractPoints(String content){
        int[][] points = new int[getFileLength()][2];
        String[] str = content.trim().split("\\s+");
        for (int i=0, pos=0; i<str.length; i++, pos++){
            points[pos][0] = Math.round(Float.parseFloat(str[++i]));
            points[pos][1] = Math.round(Float.parseFloat(str[++i]));
        }
        Arrays.sort(points, (o1, o2) -> {
            if(o1[1]!=o2[1])
                return o1[1] - o2[1];
            else
                return o2[0]-o1[0];
        });
        this.points = points;
    }

    public void normalizePoints() {
        if(points==null)
            return;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int[][] pts = new int[points.length][2];
        for(int[] pt:points){
            if(pt[0]<minX)
                minX = pt[0];
            if(pt[1]<minY)
                minY = pt[1];
            if(pt[0]>maxX)
                maxX = pt[0];
            if(pt[1]>maxY)
                maxY = pt[1];
        }
        int rangeX = (maxX-minX);
        int rangeY = (maxY-minY);
        for(int i=0;i<pts.length;i++){
            pts[i][0] = points[i][0] - (minX - 50);
            pts[i][0] = (pts[i][0]*1250)/rangeX;
            pts[i][1] = points[i][1] - (minY - 50);
            pts[i][1] = (pts[i][1]*750)/rangeY;
        }
        normalizedPoints = pts;
        setChanged();
        notifyObservers();
    }

    public void attachThread(int n){
        for(int i=0;i<n;i++){
            int s = 1+(i*(normalizedPoints.length/n));
            List<Integer> route = new ArrayList<>();
            List<Integer> cost = new ArrayList<>();
            TSPKnowledgeSource tspKnowledgeSource = new TSPKnowledgeSource(normalizedPoints, s, route, cost);
            Thread thread = new Thread(tspKnowledgeSource);
            threadList.add(thread);
            routeList.add(route);
            costList.add(cost);
            thread.start();
        }
    }

    public int[] getMinThreeCost(int n){
        int firstMin = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int[] res = new int[] {0,1,2};
        for(int k=0;k<3;k++) {
            for (int i = 0; i < costList.size(); i++) {
                if (costList.get(i).get(n-1) > firstMin && costList.get(i).get(n-1) < min) {
                    min = costList.get(i).get(n-1);
                    minIndex = i;
                }
            }
            res[k] = minIndex;
            firstMin = min;
            min = Integer.MAX_VALUE;
        }
        return res;
    }

    public int[][] getPoints() {
        return points;
    }

    public void setPoints(int[][] points) {
        this.points = points;
    }

    public int[][] getNormalizedPoints() {
        return normalizedPoints;
    }

    public void setNormalizedPoints(int[][] normalizedPoints) {
        this.normalizedPoints = normalizedPoints;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public List<Thread> getThreadList() {
        return threadList;
    }

    public void setThreadList(List<Thread> threadList) {
        this.threadList = threadList;
    }

    public List<List<Integer>> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<List<Integer>> routeList) {
        this.routeList = routeList;
    }

    public List<List<Integer>> getCostList() {
        return costList;
    }

    public void setCostList(List<List<Integer>> costList) {
        this.costList = costList;
    }
}