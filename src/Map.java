class Map {
    private char[][] map;
    private int[][] idMap;
    private int width;
    private int height;

    Map(int width, int height){
        map = new char[width][height];
        idMap = new int[width][height];
        this.width = width;
        this.height= height;
        clear();
    }

    void set(int x, int y, char val, int ID){
        map[x][y]=val;
        idMap[x][y] = ID;
    }

    char read(int x, int y){
        return map[x][y];
    }

    int readID(int x, int y){
        return idMap[x][y];
    }

    void clear(){
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                map[i][j] = ' ';
                idMap[i][j] = -1;
            }
        }
    }

    int getWidth(){
        return width;
    }

    int getHeight(){
        return height;
    }

    char[][] getSurroundings(int x, int y){
        char[][] surroundings = new char[3][3];
        for(int k=-1;k<2;k++){
            for(int l=-1;l<2;l++){
                boolean inRange = (x+k > 0 && x+k < width && y+l > 0 && y+l < height);
                if(inRange){surroundings[k+1][l+1] = read(x+k,y+l);}
                else{surroundings[k+1][l+1] = ' ';} // Animals have a blind spot here, because of screen wrap
            }
        }
        return surroundings;
    }
}
