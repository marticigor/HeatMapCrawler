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

		riop.setPixelToCheckAround(pivot);

		pixelToDisjointSet = new HashMap<Pixel, Set<Pixel>>();
		foregroundPixels = new LinkedList<Pixel>();

		fGround = 0;

		for (Pixel aroundPivot : riop) {
			if (parent.isForeground(aroundPivot)) {
				fGround++;
				// why is all this so complicated here? If I recall I have found
				// some edge case when simpler 0 to 1
				// count approach failed but cannot remember which case it was.
				disjointSet = new HashSet<Pixel>();
				disjointSet.add(aroundPivot);
				pixelToDisjointSet.put(aroundPivot, disjointSet);
				foregroundPixels.add(aroundPivot);// keep order

			}
		}

		for (Pixel p : foregroundPixels) {

			riop.setPixelToCheckAround(p);

			Set<Pixel> pSet = pixelToDisjointSet.get(p);

			for (Pixel pIn : riop) {
				if (!parent.isWithinEnvelope(pIn, pivot)) {
					// NOT WITHIN ENVELOPE
					continue;
				}

				if (parent.isForeground(pIn) && pIn != pivot) {
					// join disjoint sets (all pInSet members into pSet)
					Set<Pixel> pInSet = pixelToDisjointSet.get(pIn);
					for (Object iter : pInSet)
						pSet.add((Pixel) iter);
				}
			}
		}

		int maxSize = 0;

		for (Pixel p : foregroundPixels) {
			Set<Pixel> s = pixelToDisjointSet.get(p);
			if (s.size() > maxSize)
				maxSize = s.size();
		}

		return (false == ((int) fGround == maxSize));

	}
}
