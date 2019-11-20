package com.akingyin.tuya.shape;

/**
 * @author zlcd
 */
public enum ShapeType {
	/**
	 *
	 */
	Arrow(1, "箭头"),
	Circle(2, "圆"),
	Line(3, "线段"),
	MLine(4, "箭头折线"),
	ArrowWithTxt(5, "箭头带文字"),
	TurnAround(6, "掉头图标"),
	Mosaic(7, "马赛克"),
	NULL(-1, "无"),
	//Magnifier(8,"放大镜"),
	 Rectangle(9,"矩形")
	,BROKENLINE(10,"折线");

	public static ShapeType getShapeType(int type) {
		ShapeType[] sts = values();
		for (ShapeType t : sts) {
			if (type == t.getType()) {
				return t;
			}
		}
		return Circle;

		// ShapeType st = null;
		// switch (type) {
		// case 1: {
		// st = Arrow;
		// }
		// break;
		// case 2: {
		// st = Circle;
		// }
		// break;
		// case 3: {
		// st = Line;
		// }
		// break;
		// case 4: {
		// st = MLine;
		// }
		// break;
		// case 5: {
		// st = ArrowWithTxt;
		// }
		// break;
		// case 6: {
		// st = TurnAround;
		// }
		// break;
		// case 7: {
		// st = Mosaic;
		// }
		// break;
		// case -1: {
		// st = NULL;
		// }
		// break;
		//
		// default:
		// st = NULL;
		// break;
		// }
		// return st;
	}

	private int type;
	private String name;

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	private ShapeType(int type, String name) {
		this.type = type;
		this.name = name;
	}
}
