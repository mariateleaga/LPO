package lab09.parser.ast;

import static java.util.Objects.requireNonNull;

import lab09.visitors.Visitor;

public class DictPut extends TernaryOP {

	public DictPut(Exp dict, Exp key, Exp value) {
		super(dict, key, value);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictPut(first, second, third);
	}
}
