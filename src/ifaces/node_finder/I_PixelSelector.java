package ifacec.node_finder;

import java.util.Set;

import lib_duke.Pixel;

public interface I_PixelSelector {

	public void proces(Set<Pixel> inputSet, I_PixelExam exam, int... args);

	public Set<Pixel> getSet();

}
