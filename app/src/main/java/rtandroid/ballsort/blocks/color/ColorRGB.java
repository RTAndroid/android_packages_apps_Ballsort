package rtandroid.ballsort.blocks.color;

public class ColorRGB
{
    public int r;
    public int g;
    public int b;

    private ColorRGB(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorRGB()
    {
        this(0, 0, 0);
    }

    @Override
    public String toString()
    {
        return "(r=" + r + ", g=" + g + ", b=" + b + ")";
    }
}
