package smptk
package image

import common.{ Domain, DiscreteDomain, BoxedRegion }
import smptk.image.Geometry._
import com.sun.org.apache.xml.internal.serializer.ToStream



//case class ContinuousImageDomain1D(val isInside: CoordVector1D[Double] => Boolean) extends ContinuousImageDomain[CoordVector1D] {
//  def dimensionality = 1
//
//}
//
//case class ContinuousImageDomain2D(val isInside: CoordVector2D[Double] => Boolean) extends ContinuousImageDomain[CoordVector2D] {
//  def dimensionality = 2
//}
//
//case class ContinuousImageDomain3D(val isInside: CoordVector3D[Double] => Boolean) extends ContinuousImageDomain[CoordVector3D] {
//  def dimensionality = 3
//}

trait DiscreteImageDomain[CV[A] <: CoordVector[A]] extends DiscreteDomain[CV] with BoxedRegion[CV]{ //extends ImageDomain[Point] {

  def spacing: CV[Double]
  def size: CV[Int]

  def directions: Array[Double]

  def numberOfPoints = (0 until size.dimensionality).foldLeft(1)((res, d) => res * size(d))

  def indexToLinearIndex(idx: CV[Int]): Int
  def linearIndexToIndex(linearIdx: Int): CV[Int]

  def isInside(pt: CV[Double]): Boolean



}

case class DiscreteImageDomain1D(val origin: Point1D, val spacing: Point1D, val size: CoordVector1D[Int]) extends DiscreteImageDomain[CoordVector1D] {
  val dimensionality = 1
  def points = for (i <- (0 until size(0)).view) yield CoordVector1D(origin(0) + spacing(0) * i)

  val extent = CoordVector1D(origin(0) + spacing(0) * size(0))

  def indexToLinearIndex(idx: CoordVector1D[Int]) = idx(0)
  def linearIndexToIndex(linearIdx: Int) = CoordVector1D(linearIdx)

  val directions = Array(1.)

  def isInside(pt: CoordVector1D[Double]): Boolean = {
    pt(0) >= origin(0) && pt(0) <= extent(0)
  }


}

case class DiscreteImageDomain2D(val origin: Point2D, val spacing: CoordVector2D[Double], val size: CoordVector2D[Int]) extends DiscreteImageDomain[CoordVector2D] {
  val dimensionality = 2
  def points = for (j <- (0 until size(1)).view; i <- (0 until size(0)).view)
    yield CoordVector2D(origin(0) + spacing(0) * i, origin(1) + spacing(1) * j)

  val extent = CoordVector2D(origin(0) + spacing(0) * size(0), origin(1) + spacing(1) * size(1))

  def indexToLinearIndex(idx: CoordVector2D[Int]) = idx(0) + idx(1) * size(0)
  def linearIndexToIndex(linearIdx: Int) = (CoordVector2D(linearIdx % size(0), linearIdx / size(0)))

  val directions = Array(1., 0., 0., 1.)

  def isInside(pt: CoordVector2D[Double]): Boolean = {
    pt(0) >= origin(0) && pt(0) <= extent(0) &&
      pt(1) >= origin(1) && pt(1) <= extent(1)
  }


}

case class DiscreteImageDomain3D(val origin: Point3D, val spacing: CoordVector3D[Double], val size: CoordVector3D[Int]) extends DiscreteImageDomain[CoordVector3D] {
  val dimensionality = 3
  
  def points = for (k <- (0 until size(2)).view; j <- (0 until size(1)).toStream; i <- (0 until size(0)).view)
    yield CoordVector3D(origin(0) + spacing(0) * i, origin(1) + spacing(1) * j, origin(2) + spacing(2) * k)

  val extent = CoordVector3D(origin(0) + spacing(0) * size(0), origin(1) + spacing(1) * size(1), origin(2) + spacing(2) * size(2))
  def indexToLinearIndex(idx: CoordVector3D[Int]) = idx(0) + idx(1) * size(0) + idx(2) * size(0) * size(1)
  def linearIndexToIndex(linearIdx: Int) =
    CoordVector3D(
      linearIdx % (size(0) * size(1)) % size(0),
      linearIdx % (size(0) * size(1)) / size(0),
      linearIdx / (size(0) * size(1)))

  val directions = Array(1., 0., 0., 0., 1., 0., 0., 0., 1)

  def isInside(pt: CoordVector3D[Double]): Boolean = {
    pt(0) >= origin(0) && pt(0) <= extent(0) &&
      pt(1) >= origin(1) && pt(1) <= extent(1) &&
      pt(2) >= origin(2) && pt(2) <= extent(2)
  }
  
}
