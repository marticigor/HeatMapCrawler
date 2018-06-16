package core.image_morpho_transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.image_morpho_transform.Skeleton.SkeletonUtils;
import core.utils.RoundIteratorOfPixels;
import lib_duke.Pixel;

public class LocalyDisconnectTest {

	private final RoundIteratorOfPixels riop;
	private int fGround;

	private Map<Pixel, Set<Pixel>> pixelToDisjointSet;
	private List<Pixel> foregroundPixels;
	private Set<Pixel> disjointSet;

	private SkeletonUtils parent;

	public LocalyDisconnectTest(SkeletonUtils parentToCallBack) {
		parent = parentToCallBack;
		riop = new RoundIteratorOfPixels();
		riop.setImageResource(parent.getImageResource());
	}

	/**
	 * 
	 * @param pivot
	 * @return
	 */
	public boolean locallyDisconnects(Pixel pivot) {
		// simple iterative counting of "up, down edges" fails in some important
		// cases

		// resetLog();

		riop.setPixelToCheckAround(pivot);

		pixelToDisjointSet = new HashMap<Pixel, Set<Pixel>>();
		foregroundPixels = new LinkedList<Pixel>();

		fGround = 0;

		for (Pixel aroundPivot : riop) {
			if (parent.isForeground(aroundPivot)) {
				fGround++;
				disjointSet = new HashSet<Pixel>();
				disjointSet.add(aroundPivot);
				pixelToDisjointSet.put(aroundPivot, disjointSet);
				foregroundPixels.add(aroundPivot);// keep order
			}
		}

		if (fGround == 1)
			return true;

		for (Pixel pixForeground : foregroundPixels) {
			riop.setPixelToCheckAround(pixForeground);
			Set<Pixel> pixForegroundSet = pixelToDisjointSet.get(pixForeground);

			for (Pixel pixWithinEnvelope : riop) {
				if (!parent.isWithinEnvelope(pixWithinEnvelope, pivot)) {
					// NOT WITHIN ENVELOPE
					continue;
				}

				// YES, WITHIN ENVELOPE
				if (parent.isForeground(pixWithinEnvelope) && pixWithinEnvelope != pivot) {
					// join disjoint sets (all pixWithinEnvelopeSet members into
					// pixForegroundSet)
					Set<Pixel> pixWithinEnvelopeSet = pixelToDisjointSet.get(pixWithinEnvelope);
					for (Pixel p : pixWithinEnvelopeSet)
						pixForegroundSet.add(p);
					// pixForegroundSet is now a set of all Pixels connected to
					// pixForeground
					for (Pixel connected : pixForegroundSet) {
						pixelToDisjointSet.put(connected, pixForegroundSet);
					}
				}
			}
		}

		int maxSize = 0;
		for (Pixel p : foregroundPixels) {
			Set<Pixel> s = pixelToDisjointSet.get(p);
			if (s.size() > maxSize)
				maxSize = s.size();
		}
		return (fGround != maxSize);
	}

	private List<String> messages = new LinkedList<String>();

	@SuppressWarnings("unused")
	private void log(String message) {
		messages.add(message);
	}

	@SuppressWarnings("unused")
	private void resetLog() {
		messages.clear();
	}

	@SuppressWarnings("unused")
	private void dumpLog() {
		for (String message : messages)
			System.out.println("LOC_DISC_TEST: " + message);
	}

	@SuppressWarnings("unused")
	private String setToString(Set<Pixel> mySet) {
		String concat = "\n";
		for (Pixel p : mySet)
			concat += p.toString() + "\n";
		return concat;
	}
}
