package com.mrcrayfish.modelcreator.element;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import com.mrcrayfish.modelcreator.util.FaceDimension;

public class Element
{
	private String name = "Cube";

	// Face Variables
	private int selectedFace = 0;
	private Face[] faces = new Face[6];

	// Element Variables
	private double startX = 0.0, startY = 0.0, startZ = 0.0;
	private double width = 16.0, height = 1.0, depth = 1.0;

	// Rotation Variables
	private double originX = 8, originY = 8, originZ = 8;
	private double rotation;
	private int axis = 0;
	private boolean rescale = false;

	// Extra Variables
	private boolean shade = true;

	// Rotation Point Indicator
	private Sphere sphere = new Sphere();

	public Element(double width, double height, double depth)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
		initFaces();
	}

	public Element(Element cuboid)
	{
		this.width = cuboid.getWidth();
		this.height = cuboid.getHeight();
		this.depth = cuboid.getDepth();
		this.startX = cuboid.getStartX();
		this.startY = cuboid.getStartY();
		this.startZ = cuboid.getStartZ();
		this.originX = cuboid.getOriginX();
		this.originY = cuboid.getOriginY();
		this.originZ = cuboid.getOriginZ();
		this.rotation = cuboid.getRotation();
		this.axis = cuboid.getPrevAxis();
		this.rescale = cuboid.shouldRescale();
		this.shade = cuboid.isShaded();
		this.selectedFace = cuboid.getSelectedFaceIndex();
		initFaces();
		for (int i = 0; i < faces.length; i++)
		{
			Face oldFace = cuboid.getAllFaces()[i];
			faces[i].fitTexture(oldFace.shouldFitTexture());
			faces[i].setTexture(oldFace.getTextureName());
			faces[i].setTextureLocation(oldFace.getTextureLocation());
			faces[i].addTextureX(oldFace.getStartU());
			faces[i].addTextureY(oldFace.getStartV());
			faces[i].setCullface(oldFace.isCullfaced());
			faces[i].setEnabled(oldFace.isEnabled());
		}
	}

	public void initFaces()
	{
		for (int i = 0; i < faces.length; i++)
			faces[i] = new Face(this, i);
	}

	public void setSelectedFace(int face)
	{
		this.selectedFace = face;
	}

	public Face getSelectedFace()
	{
		return faces[selectedFace];
	}

	public int getSelectedFaceIndex()
	{
		return selectedFace;
	}

	public Face[] getAllFaces()
	{
		return faces;
	}

	public int getLastValidFace()
	{
		int id = 0;
		for (Face face : faces)
		{
			if (face.isEnabled())
			{
				id = face.getSide();
			}
		}
		return id;
	}

	public FaceDimension getFaceDimension(int side)
	{
		switch (side)
		{
		case 0:
			return new FaceDimension(getWidth(), getHeight());
		case 1:
			return new FaceDimension(getDepth(), getHeight());
		case 2:
			return new FaceDimension(getWidth(), getHeight());
		case 3:
			return new FaceDimension(getDepth(), getHeight());
		case 4:
			return new FaceDimension(getWidth(), getDepth());
		case 5:
			return new FaceDimension(getWidth(), getDepth());
		}
		return null;
	}

	public void clearAllTextures()
	{
		for (Face face : faces)
		{
			face.setTexture(null);
		}
	}

	public void setAllTextures(String texture)
	{
		for (Face face : faces)
		{
			face.setTexture(texture);
		}
	}

	public void draw()
	{
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_CULL_FACE);
			GL11.glTranslated(getOriginX(), getOriginY(), -getOriginZ());
			rotateAxis();
			GL11.glTranslated(-getOriginX(), -getOriginY(), getOriginZ());

			// North
			if (faces[0].isEnabled())
			{
				GL11.glColor3f(0, 1, 0);
				faces[0].renderNorth();
			}

			// East
			if (faces[1].isEnabled())
			{
				GL11.glColor3f(0, 0, 1);
				faces[1].renderEast();
			}

			// South
			if (faces[2].isEnabled())
			{
				GL11.glColor3f(1, 0, 0);
				faces[2].renderSouth();
			}

			// West
			if (faces[3].isEnabled())
			{
				GL11.glColor3f(1, 1, 0);
				faces[3].renderWest();
			}

			// Top
			if (faces[4].isEnabled())
			{
				GL11.glColor3f(0, 1, 1);
				faces[4].renderUp();
			}

			// Bottom
			if (faces[5].isEnabled())
			{
				GL11.glColor3f(1, 0, 1);
				faces[5].renderDown();
			}
		}
		GL11.glPopMatrix();
	}

	public void drawExtras(ElementManager manager)
	{
		if (manager.getSelectedCuboid() == this)
		{
			GL11.glPushMatrix();
			{
				GL11.glTranslated(getOriginX(), getOriginY(), -getOriginZ());
				GL11.glColor3f(0.25F, 0.25F, 0.25F);
				sphere.draw(0.2F, 16, 16);
				rotateAxis();
				GL11.glLineWidth(2F);
				GL11.glBegin(GL_LINES);
				{
					GL11.glColor3f(1, 0, 0);
					GL11.glVertex3i(-4, 0, 0);
					GL11.glVertex3i(4, 0, 0);
					GL11.glColor3f(0, 1, 0);
					GL11.glVertex3i(0, -4, 0);
					GL11.glVertex3i(0, 4, 0);
					GL11.glColor3f(0, 0, 1);
					GL11.glVertex3i(0, 0, -4);
					GL11.glVertex3i(0, 0, 4);
				}
				GL11.glEnd();
			}
			GL11.glPopMatrix();
		}
	}

	public void addStartX(double amt)
	{
		this.startX += amt;
	}

	public void addStartY(double amt)
	{
		this.startY += amt;
	}

	public void addStartZ(double amt)
	{
		this.startZ += amt;
	}

	public double getStartX()
	{
		return startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public double getStartZ()
	{
		return startZ;
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public double getDepth()
	{
		return depth;
	}

	public void addWidth(double amt)
	{
		this.width += amt;
	}

	public void addHeight(double amt)
	{
		this.height += amt;
	}

	public void addDepth(double amt)
	{
		this.depth += amt;
	}

	public double getOriginX()
	{
		return originX;
	}

	public void addOriginX(double amt)
	{
		this.originX += amt;
	}

	public double getOriginY()
	{
		return originY;
	}

	public void addOriginY(double amt)
	{
		this.originY += amt;
	}

	public double getOriginZ()
	{
		return originZ;
	}

	public void addOriginZ(double amt)
	{
		this.originZ += amt;
	}

	public double getRotation()
	{
		return rotation;
	}

	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}

	public int getPrevAxis()
	{
		return axis;
	}

	public void setPrevAxis(int prevAxis)
	{
		this.axis = prevAxis;
	}

	public void setRescale(boolean rescale)
	{
		this.rescale = rescale;
	}

	public boolean shouldRescale()
	{
		return rescale;
	}

	public boolean isShaded()
	{
		return shade;
	}

	public void setShade(boolean shade)
	{
		this.shade = shade;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public void rotateAxis()
	{
		switch (axis)
		{
		case 0:
			GL11.glRotated(getRotation(), 1, 0, 0);
			break;
		case 1:
			GL11.glRotated(getRotation(), 0, 1, 0);
			break;
		case 2:
			GL11.glRotated(getRotation(), 0, 0, 1);
			break;
		}
	}

	public static String parseAxis(int axis)
	{
		switch (axis)
		{
		case 0:
			return "x";
		case 1:
			return "y";
		case 2:
			return "z";
		}
		return "x";
	}

	public Element copy()
	{
		return new Element(getWidth(), getHeight(), getDepth());
	}
}
