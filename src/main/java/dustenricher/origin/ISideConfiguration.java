package dustenricher.origin;

public interface ISideConfiguration
{
	/**
	 * Gets the tile's configuration component.
	 * @return the tile's configuration component
	 */
	public TileComponentConfig getConfig();

	/**
	 * Gets this machine's current orientation.
	 * @return machine's current orientation
	 */
	public int getOrientation();

	/**
	 * Gets this machine's ejector.
	 * @return
	 */
	public TileComponentEjector getEjector();
}
