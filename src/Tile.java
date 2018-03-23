
public class Tile {
	private int value = 0;
	protected boolean flagged = false;
	protected boolean uncovered = false;
	
	public void set_value(int value)
	{
		this.value = value;
	}
	
	public int get_value()
	{
		return value;
	}
	
	public void set_flag(boolean flag)
	{
		flagged = flag;
	}
	
	public boolean is_flagged()
	{
		return flagged;
	}
	
	public void uncover()
	{
		uncovered = true;
	}
	
	public boolean is_uncovered()
	{
		return uncovered;
	}
}
