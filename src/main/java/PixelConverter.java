
public class PixelConverter {

    public static int colorsList[]={0,4796129,2472996,3390408,16529695,13842643,11162880,11184810,5592405,5592575,5635925,5636095,16733525,16733695,16777045,16777215 };

 //           11382055,12727841,13842643,3390408,13356237,8487811,16529695,3270434,15395875,5780479,16332280,1372400,15330283,16777215};
    public static String namedColorList[]={"0;30","0;34","0;32","0;36","0;31","0;35","0;37","1;30","1;34","1;32","1;36","1;36","1;31","1;31","1:35","1;33","1;37"};


    private static int getColorScore(int color,int red,int blue,int green)
    {
             return Math.abs(red - (color >> 16) & 0xFF) + Math.abs(green - (color>> 8) & 0xFF) + Math.abs(blue - (color & 0xFF));
    }

    public static String getTranslatedColor(int red,int blue,int green)
    {
         int topScore=Integer.MAX_VALUE;
         int score;
         int colorIndex=0;
          for (int i=0; i < colorsList.length; i++){
              score=getColorScore(colorsList[i],red,blue,green);
              if(score < topScore)
              {
                   colorIndex=i;
                   topScore=score;
              }
          }

         return namedColorList[colorIndex];
    }




}
