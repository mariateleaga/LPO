package lab09.parser.ast;

import lab09.visitors.Visitor;

public class DictDel extends BinaryOp {
	public DictDel(Exp dict, Exp key) {
		super(dict, key);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictDel(left, right);
	}
}