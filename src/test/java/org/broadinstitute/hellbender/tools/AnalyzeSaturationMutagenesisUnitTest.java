package org.broadinstitute.hellbender.tools;

import org.broadinstitute.hellbender.GATKBaseTest;
import org.broadinstitute.hellbender.tools.AnalyzeSaturationMutagenesis.Interval;
import org.broadinstitute.hellbender.tools.AnalyzeSaturationMutagenesis.IntervalCounter;
import org.broadinstitute.hellbender.tools.AnalyzeSaturationMutagenesis.SNV;
import org.broadinstitute.hellbender.tools.AnalyzeSaturationMutagenesis.SNVCollectionCount;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyzeSaturationMutagenesisUnitTest extends GATKBaseTest {
    @Test
    public void testInterval() {
        final Interval interval = new Interval(1, 3);
        Assert.assertEquals(interval.size(), 2);
    }

    @Test
    public void testSNV() {
        final SNV snv1 = new SNV(0, (byte)'A', (byte)'C', (byte)30);
        final SNV snv2 = new SNV(0, (byte)'A', (byte)'C', (byte)20);
        Assert.assertEquals(snv1.hashCode(), snv2.hashCode());
        Assert.assertEquals(snv1, snv2);
        Assert.assertEquals(snv1.compareTo(snv2), 0);
        final SNV snv3 = new SNV(1, (byte)'A', (byte)'C', (byte)30);
        Assert.assertNotEquals(snv1.hashCode(), snv3.hashCode());
        Assert.assertNotEquals(snv1, snv3);
        Assert.assertTrue(snv1.compareTo(snv3) < 0);
        Assert.assertTrue(snv3.compareTo(snv2) > 0);
        final SNV snv4 = new SNV(0, (byte)'G', (byte)'C', (byte)30);
        Assert.assertNotEquals(snv1.hashCode(), snv4.hashCode());
        Assert.assertNotEquals(snv1, snv4);
        Assert.assertTrue(snv1.compareTo(snv4) < 0);
        Assert.assertTrue(snv4.compareTo(snv1) > 0);
        final SNV snv5 = new SNV(0, (byte)'A', (byte)'G', (byte)30);
        Assert.assertNotEquals(snv1.hashCode(), snv5.hashCode());
        Assert.assertNotEquals(snv1, snv5);
        Assert.assertTrue(snv1.compareTo(snv5) < 0);
        Assert.assertTrue(snv5.compareTo(snv1) > 0);
    }

    @Test
    public void testIntervalCounter() {
        final IntervalCounter intervalCounter = new IntervalCounter(10);
        intervalCounter.addCount(0, 10);
        intervalCounter.addCount(1, 9);
        intervalCounter.addCount(2, 8);
        intervalCounter.addCount(3, 7);
        intervalCounter.addCount(4, 6);
        Assert.assertEquals(intervalCounter.countSpanners(0, 10), 1);
        Assert.assertEquals(intervalCounter.countSpanners(5, 5), 5);
        Assert.assertEquals(intervalCounter.countSpanners(2, 5), 3);
        Assert.assertEquals(intervalCounter.countSpanners(5, 8), 3);
        intervalCounter.addCount(0, 10);
        Assert.assertEquals(intervalCounter.countSpanners(0, 10), 2);
    }

    @Test
    public void testSNVCollection() {
        final List<SNV> snvList = new ArrayList<>(Arrays.asList(
                new SNV(0, (byte)'A', (byte)'C', (byte)30),
                new SNV(1, (byte)'A', (byte)'C', (byte)20)));
        final SNVCollectionCount cc1 = new SNVCollectionCount(snvList, 10);

        // equality, key, compare, and hash should be independent of count and coverage
        final List<SNV> snvList2 = Arrays.asList(
                new SNV(0, (byte)'A', (byte)'C', (byte)30),
                new SNV(1, (byte)'A', (byte)'C', (byte)20));
        final SNVCollectionCount cc2 = new SNVCollectionCount(snvList2, 20);
        Assert.assertEquals(cc1.hashCode(), cc2.hashCode());
        Assert.assertEquals(cc1, cc2);
        Assert.assertEquals(cc1.compareTo(cc2), 0);
        cc2.bumpCount(30);
        Assert.assertEquals(cc1.hashCode(), cc2.hashCode());
        Assert.assertEquals(cc1, cc2);
        Assert.assertEquals(cc1.compareTo(cc2), 0);

        Assert.assertEquals(cc2.getCount(), 2);
        Assert.assertEquals(cc2.getMeanRefCoverage(), 25., .0000001);

        // changing the list shouldn't change the hash or the key
        final int cc1Hash = cc1.hashCode();
        final List<SNV> key1 = cc1.getSNVs();
        snvList.add(new SNV(2, (byte)'A', (byte)'C', (byte)10));
        Assert.assertEquals(cc1.hashCode(), cc1Hash);
        Assert.assertEquals(cc1.getSNVs(), key1);

        // different lists should mean unequal to each other, unequal hashes, and non-zero compare
        final SNVCollectionCount cc3 = new SNVCollectionCount(snvList, 20);
        Assert.assertNotEquals(cc1.hashCode(), cc3.hashCode());
        Assert.assertNotEquals(cc1, cc3);
        Assert.assertTrue(cc1.compareTo(cc3) < 0);
        Assert.assertTrue(cc3.compareTo(cc1) > 0);
    }
}
