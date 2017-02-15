package su.hm.hprob;

/**
 * There are just some constants which are used to config.
 * Created by hm-su on 2017/2/14.
 */

final class HpbConstants {

    private HpbConstants() {
        throw new AssertionError();
    }

    /**
     * custom attr is not provided.
     */
    static final int ATTR_NOT_PROVIDE = -1;

    /**
     * some values are zero.
     */
    static final int VAL_ZERO = 0;

    static final int TEXT_TOP_GAP = 6;

    static final String PERCENT_CHAR = "%";

    /**
     * When text height is larger than bar height, it will be used.
     */
    static final float FONT_SIZE_OFFSET = 4f;
}
