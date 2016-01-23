// $Id: Matrix3d.java,v 1.3 2012-07-12 20:13:01 falk Exp $

/**
 * 3d transformation matrices.
 */

package org.efalk.math;

import java.lang.Math;

import android.util.Log;

public class Matrix3d {
    private static final String TAG = "FlightDeck";
    private static final double[] iMat = {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
    public double[] rMat = new double[16];
    private double[] t1 = new double[16];	// temporary
    //private double[] t2 = new double[16];

    public Matrix3d() {
	reset();
    }

    /**
     * Reset transform to unity
     */
    public void reset() {
	System.arraycopy(iMat, 0, rMat, 0, 16);
    }

    /**
     * Return the current transformation matrix.
     * Note: this returns a reference to the internal matrix, which
     * could theoretically be modified by the caller, affecting
     * the internal value.
     */
    public double[] getMat() {
	return rMat;
    }

    /**
     * Replace the current transformation matrix.
     * Note: this copies a reference to the original matrix, which
     * could theoretically be modified by the caller, affecting
     * the internal value.
     */
    public void setMat(double[] rMat) {
	this.rMat = rMat;
    }

    /**
     * Replace the current transformation matrix
     */
    public void setMat(float[] mat) {
	for( int i = 0; i < 16; ++i )
	    rMat[i] = mat[i];
    }

    /**
     * Transform the 3d vertices contained in this array.
     * Dst and src may be the same.
     */
    public void xform(double[] src, double[] dst) {
	for(int i=0; i<src.length; i += 3) {
	    double x = src[i];
	    double y = src[i+1];
	    double z = src[i+2];
	    dst[i]   = x*rMat[0] + y*rMat[4] + z*rMat[8] + rMat[12];
	    dst[i+1] = x*rMat[1] + y*rMat[5] + z*rMat[9] + rMat[13];
	    dst[i+2] = x*rMat[2] + y*rMat[6] + z*rMat[10] + rMat[14];
	}
    }

    public void xform(float[] src, float[] dst) {
	for(int i=0; i<src.length; i += 3) {
	    double x = src[i];
	    double y = src[i+1];
	    double z = src[i+2];
	    dst[i]   = (float)(x*rMat[0] + y*rMat[4] + z*rMat[8] + rMat[12]);
	    dst[i+1] = (float)(x*rMat[1] + y*rMat[5] + z*rMat[9] + rMat[13]);
	    dst[i+2] = (float)(x*rMat[2] + y*rMat[6] + z*rMat[10] + rMat[14]);
	}
    }

    /**
     * Rotate about X; this is a post-multiply, i.e. world
     * coordinates not model coordinates.
     */
    public void rotX(double theta) {
	//rotX(theta, t2);
	//matMul(t1, rMat, t2);
	// a b c d  1 0  0 0
	// e f g h  0 c -s 0
	// i j k l  0 s  c 0
	// m n o p  0 0  0 1
	double c = Math.cos(theta);
	double s = Math.sin(theta);
	t1[0] = rMat[0];
	t1[1] = rMat[1]*c + rMat[2]*s;
	t1[2] = rMat[1]*-s + rMat[2]*c;
	t1[3] = rMat[3];
	t1[4] = rMat[4];
	t1[5] = rMat[5]*c + rMat[6]*s;
	t1[6] = rMat[5]*-s + rMat[6]*c;
	t1[7] = rMat[7];
	t1[8] = rMat[8];
	t1[9] = rMat[9]*c + rMat[10]*s;
	t1[10] = rMat[9]*-s + rMat[10]*c;
	t1[11] = rMat[11];
	t1[12] = rMat[12];
	t1[13] = rMat[13]*c + rMat[14]*s;
	t1[14] = rMat[13]*-s + rMat[14]*c;
	t1[15] = rMat[15];
	double[] tmp = rMat; rMat = t1; t1 = tmp;
    }

    /**
     * Compute rotation matrix about X.
     */
    public static void rotX(double theta, double[] mat) {
	double c = Math.cos(theta);
	double s = Math.sin(theta);
	mat[0] = 1; mat[1] = 0; mat[2] = 0; mat[3] = 0;
	mat[4] = 0; mat[5] = c; mat[6] = -s; mat[7] = 0;
	mat[8] = 0; mat[9] = s; mat[10] = c; mat[11] = 0;
	mat[12] = 0; mat[13] = 0; mat[14] = 0; mat[15] = 1;
    }

    /**
     * Compute rotation matrix about X
     */
    public static void rotX(double theta, Matrix3d mat) {
	rotX(theta, mat.rMat);
    }

    /**
     * Rotate about Y.  This is a post-multiply, i.e.
     * world coordinates, not model coordinates.
     */
    public void rotY(double theta) {
	// rotY(theta, t2);
	// matMul(t1, rMat, t2);
	double c = Math.cos(theta);
	double s = Math.sin(theta);
	// a b c d  c 0 s 0
	// e f g h  0 1 0 0
	// i j k l -s 0 c 0
	// m n o p  0 0 0 1
	//
	// for( i=0; i<4; ++i ) {
	//     for( j=0; j<4; ++j ) {
	// 	float v = 0;
	// 	for( k=0; k<4; ++k )
	// 	    v += m1[k+4*j] * m2[i+4*k];
	// 	dst[i+4*j] = v;
	//     }
	// }
	t1[0] = rMat[0]*c + rMat[2]*-s;
	t1[1] = rMat[1];
	t1[2] = rMat[0]*s + rMat[2]*c;
	t1[3] = rMat[3];
	t1[4] = rMat[4]*c + rMat[6]*-s;
	t1[5] = rMat[5];
	t1[6] = rMat[4]*s + rMat[6]*c;
	t1[7] = rMat[7];
	t1[8] = rMat[8]*c + rMat[10]*-s;
	t1[9] = rMat[9];
	t1[10] = rMat[8]*s + rMat[10]*c;
	t1[11] = rMat[11];
	t1[12] = rMat[12]*c + rMat[14]*-s;
	t1[13] = rMat[13];
	t1[14] = rMat[12]*s + rMat[14]*c;
	t1[15] = rMat[15];
	double[] tmp = rMat; rMat = t1; t1 = tmp;
    }

    /**
     * Compute rotation matrix aboutY
     */
    public static void rotY(double theta, double[] mat) {
	double c = Math.cos(theta);
	double s = Math.sin(theta);
	mat[0] = c; mat[1] = 0; mat[2] = s; mat[3] = 0;
	mat[4] = 0; mat[5] = 1; mat[6] = 0; mat[7] = 0;
	mat[8] = -s; mat[9] = 0; mat[10] = c; mat[11] = 0;
	mat[12] = 0; mat[13] = 0; mat[14] = 0; mat[15] = 1;
    }

    /**
     * Compute rotation matrix about Y
     */
    public static void rotY(double theta, Matrix3d mat) {
	rotY(theta, mat.rMat);
    }

    /**
     * Rotate about Z.  This is a post-multiply, i.e.
     * world coordinates, not model coordinates.
     */
    public void rotZ(double theta) {
	// rotZ(theta, t2);
	// matMul(t1, rMat, t2);
	// a b c d  c -s 0 0
	// e f g h  s  c 0 0
	// i j k l  0  0 1 0
	// m n o p  0  0 0 1
	double c = Math.cos(theta);
	double s = Math.sin(theta);
	t1[0] = rMat[0]*c + rMat[1]*s;
	t1[1] = rMat[0]*-s + rMat[1]*c;
	t1[2] = rMat[2];
	t1[3] = rMat[3];
	t1[4] = rMat[4]*c + rMat[5]*s;
	t1[5] = rMat[4]*-s + rMat[5]*c;
	t1[6] = rMat[6];
	t1[7] = rMat[7];
	t1[8] = rMat[8]*c + rMat[9]*s;
	t1[9] = rMat[8]*-s + rMat[9]*c;
	t1[10] = rMat[10];
	t1[11] = rMat[11];
	t1[12] = rMat[12]*c + rMat[13]*s;
	t1[13] = rMat[12]*-s + rMat[13]*c;
	t1[14] = rMat[14];
	t1[15] = rMat[15];
	double[] tmp = rMat; rMat = t1; t1 = tmp;
    }

    /**
     * Compute rotation matrix about Z
     */
    public static void rotZ(double theta, double[] mat) {
	double c = Math.cos(theta);
	double s = Math.sin(theta);
	mat[0] = c; mat[1] = -s; mat[2] = 0; mat[3] = 0;
	mat[4] = s; mat[5] = c; mat[6] = 0; mat[7] = 0;
	mat[8] = 0; mat[9] = 0; mat[10] = 1; mat[11] = 0;
	mat[12] = 0; mat[13] = 0; mat[14] = 0; mat[15] = 1;
    }

    /**
     * Compute rotation matrix about Z
     */
    public static void rotZ(double theta, Matrix3d mat) {
	rotZ(theta, mat.rMat);
    }

    public static void matMul(Matrix3d dst, Matrix3d m1, Matrix3d m2) {
	matMul(dst.rMat, m1.rMat, m2.rMat);
    }

    public static Matrix3d matMul(Matrix3d m1, Matrix3d m2) {
	Matrix3d rval = new Matrix3d();
	matMul(rval.rMat, m1.rMat, m2.rMat);
	return rval;
    }

    /**
     * Full multiply of two 4x4 matrices.
     * dst must not be m1 or m2.
     */
    public static void matMul(double[] dst, double[] m1, double[] m2) {
	// a b c d  a b c d
	// e f g h  e f g h
	// i j k l  i j k l
	// m n o p  m n o p
	//
	// for( i=0; i<4; ++i ) {
	//     for( j=0; j<4; ++j ) {
	// 	float v = 0;
	// 	for( k=0; k<4; ++k )
	// 	    v += m1[k+4*j] * m2[i+4*k];
	// 	dst[i+4*j] = v;
	//     }
	// }
	dst[0] = m1[0]*m2[0] + m1[1]*m2[4] + m1[2]*m2[8] + m1[3]*m2[12];
	dst[1] = m1[0]*m2[1] + m1[1]*m2[5] + m1[2]*m2[9] + m1[3]*m2[13];
	dst[2] = m1[0]*m2[2] + m1[1]*m2[6] + m1[2]*m2[10] + m1[3]*m2[14];
	dst[3] = m1[0]*m2[3] + m1[1]*m2[7] + m1[2]*m2[11] + m1[3]*m2[15];
	dst[4] = m1[4]*m2[0] + m1[5]*m2[4] + m1[6]*m2[8] + m1[7]*m2[12];
	dst[5] = m1[4]*m2[1] + m1[5]*m2[5] + m1[6]*m2[9] + m1[7]*m2[13];
	dst[6] = m1[4]*m2[2] + m1[5]*m2[6] + m1[6]*m2[10] + m1[7]*m2[14];
	dst[7] = m1[4]*m2[3] + m1[5]*m2[7] + m1[6]*m2[11] + m1[7]*m2[15];
	dst[8] = m1[8]*m2[0] + m1[9]*m2[4] + m1[10]*m2[8] + m1[11]*m2[12];
	dst[9] = m1[8]*m2[1] + m1[9]*m2[5] + m1[10]*m2[9] + m1[11]*m2[13];
	dst[10] = m1[8]*m2[2] + m1[9]*m2[6] + m1[10]*m2[10] + m1[11]*m2[14];
	dst[11] = m1[8]*m2[3] + m1[9]*m2[7] + m1[10]*m2[11] + m1[11]*m2[15];
	dst[12] = m1[12]*m2[0] + m1[13]*m2[4] + m1[14]*m2[8] + m1[15]*m2[12];
	dst[13] = m1[12]*m2[1] + m1[13]*m2[5] + m1[14]*m2[9] + m1[15]*m2[13];
	dst[14] = m1[12]*m2[2] + m1[13]*m2[6] + m1[14]*m2[10] + m1[15]*m2[14];
	dst[15] = m1[12]*m2[3] + m1[13]*m2[7] + m1[14]*m2[11] + m1[15]*m2[15];
    }

    /**
     * Multiply of two 4x4 matrices when we know certain values are zero
     * or one (no perspective).
     * dst must not be m1 or m2.
     */
    public static void matMulNP(double[] dst, double[] m1, double[] m2) {
	// a b c 0  a b c 0
	// e f g 0  e f g 0
	// i j k 0  i j k 0
	// m n o 1  m n o 1
	dst[0] = m1[0]*m2[0] + m1[1]*m2[4] + m1[2]*m2[8];
	dst[1] = m1[0]*m2[1] + m1[1]*m2[5] + m1[2]*m2[9];
	dst[2] = m1[0]*m2[2] + m1[1]*m2[6] + m1[2]*m2[10];
	dst[3] = 0;
	dst[4] = m1[4]*m2[0] + m1[5]*m2[4] + m1[6]*m2[8];
	dst[5] = m1[4]*m2[1] + m1[5]*m2[5] + m1[6]*m2[9];
	dst[6] = m1[4]*m2[2] + m1[5]*m2[6] + m1[6]*m2[10];
	dst[7] = 0;
	dst[8] = m1[8]*m2[0] + m1[9]*m2[4] + m1[10]*m2[8];
	dst[9] = m1[8]*m2[1] + m1[9]*m2[5] + m1[10]*m2[9];
	dst[10] = m1[8]*m2[2] + m1[9]*m2[6] + m1[10]*m2[10];
	dst[11] = 0;
	dst[12] = m1[12]*m2[0] + m1[13]*m2[4] + m1[14]*m2[8] + m2[12];
	dst[13] = m1[12]*m2[1] + m1[13]*m2[5] + m1[14]*m2[9] + m2[13];
	dst[14] = m1[12]*m2[2] + m1[13]*m2[6] + m1[14]*m2[10] + m2[14];
	dst[15] = 1;
    }

    public void fromQuaternion(float[] q) {
	float x = q[0], y = q[1], z = q[2], w;
	if (q.length == 4)
	    w = q[3];
	else {
            float mag = x*x + y*y + z*z;
            if (mag > 1)
                throw new java.lang.ArithmeticException("vector magnitude > 1");
            w = (float)Math.sqrt(1 - mag);
	}
	rMat[0] = 1 - 2*y*y - 2*z*z;
	rMat[1] = 2*x*y - 2*z*w;
	rMat[2] = 2*x*z + 2*y*w;
	rMat[3] = 0;
	rMat[4] = 2*x*y + 2*z*w;
	rMat[5] = 1 - 2*x*x - 2*z*z;
	rMat[6] = 2*y*z - 2*x*w;
	rMat[7] = 0;
	rMat[8] = 2*x*z - 2*y*w;
	rMat[9] = 2*y*z + 2*x*w;
	rMat[10] = 1 - 2*x*x - 2*y*y;
	rMat[11] = 0;
	rMat[12] = 0;
	rMat[13] = 0;
	rMat[14] = 0;
	rMat[15] = 1;
    }

    public void dump() {
	Log.d(TAG, String.format("  %5.2f  %5.2f  %5.2f  %5.2f",
		rMat[0], rMat[1], rMat[2], rMat[3]));
	Log.d(TAG, String.format("  %5.2f  %5.2f  %5.2f  %5.2f",
		rMat[4], rMat[5], rMat[6], rMat[7]));
	Log.d(TAG, String.format("  %5.2f  %5.2f  %5.2f  %5.2f",
		rMat[8], rMat[9], rMat[10], rMat[11]));
	Log.d(TAG, String.format("  %5.2f  %5.2f  %5.2f  %5.2f",
		rMat[12], rMat[13], rMat[14], rMat[15]));
    }
}
