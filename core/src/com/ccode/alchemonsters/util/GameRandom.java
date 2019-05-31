package com.ccode.alchemonsters.util;

import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class GameRandom {

	private GameRandom() {}
	
	private static final Random RAND = new Random();

	/**
	 * @return
	 * @see java.util.Random#doubles()
	 */
	public static DoubleStream doubles() {
		return RAND.doubles();
	}

	/**
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 * @see java.util.Random#doubles(double, double)
	 */
	public static DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
		return RAND.doubles(randomNumberOrigin, randomNumberBound);
	}

	/**
	 * @param streamSize
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 * @see java.util.Random#doubles(long, double, double)
	 */
	public static DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
		return RAND.doubles(streamSize, randomNumberOrigin, randomNumberBound);
	}

	/**
	 * @param streamSize
	 * @return
	 * @see java.util.Random#doubles(long)
	 */
	public static DoubleStream doubles(long streamSize) {
		return RAND.doubles(streamSize);
	}

	/**
	 * @return
	 * @see java.util.Random#ints()
	 */
	public static IntStream ints() {
		return RAND.ints();
	}

	/**
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 * @see java.util.Random#ints(int, int)
	 */
	public static IntStream ints(int randomNumberOrigin, int randomNumberBound) {
		return RAND.ints(randomNumberOrigin, randomNumberBound);
	}

	/**
	 * @param streamSize
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 * @see java.util.Random#ints(long, int, int)
	 */
	public static IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
		return RAND.ints(streamSize, randomNumberOrigin, randomNumberBound);
	}

	/**
	 * @param streamSize
	 * @return
	 * @see java.util.Random#ints(long)
	 */
	public static IntStream ints(long streamSize) {
		return RAND.ints(streamSize);
	}

	/**
	 * @return
	 * @see java.util.Random#longs()
	 */
	public static LongStream longs() {
		return RAND.longs();
	}

	/**
	 * @param streamSize
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 * @see java.util.Random#longs(long, long, long)
	 */
	public static LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
		return RAND.longs(streamSize, randomNumberOrigin, randomNumberBound);
	}

	/**
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 * @see java.util.Random#longs(long, long)
	 */
	public static LongStream longs(long randomNumberOrigin, long randomNumberBound) {
		return RAND.longs(randomNumberOrigin, randomNumberBound);
	}

	/**
	 * @param streamSize
	 * @return
	 * @see java.util.Random#longs(long)
	 */
	public static LongStream longs(long streamSize) {
		return RAND.longs(streamSize);
	}

	/**
	 * @return
	 * @see java.util.Random#nextBoolean()
	 */
	public static boolean nextBoolean() {
		return RAND.nextBoolean();
	}

	/**
	 * @param bytes
	 * @see java.util.Random#nextBytes(byte[])
	 */
	public static void nextBytes(byte[] bytes) {
		RAND.nextBytes(bytes);
	}

	/**
	 * @return
	 * @see java.util.Random#nextDouble()
	 */
	public static double nextDouble() {
		return RAND.nextDouble();
	}

	/**
	 * @return
	 * @see java.util.Random#nextFloat()
	 */
	public static float nextFloat() {
		return RAND.nextFloat();
	}

	/**
	 * @return
	 * @see java.util.Random#nextGaussian()
	 */
	public static double nextGaussian() {
		return RAND.nextGaussian();
	}

	/**
	 * @return
	 * @see java.util.Random#nextInt()
	 */
	public static int nextInt() {
		return RAND.nextInt();
	}

	/**
	 * @param bound
	 * @return
	 * @see java.util.Random#nextInt(int)
	 */
	public static int nextInt(int bound) {
		return RAND.nextInt(bound);
	}

	/**
	 * @return
	 * @see java.util.Random#nextLong()
	 */
	public static long nextLong() {
		return RAND.nextLong();
	}

	/**
	 * @param seed
	 * @see java.util.Random#setSeed(long)
	 */
	public static void setSeed(long seed) {
		RAND.setSeed(seed);
	}
	
	
	
}
