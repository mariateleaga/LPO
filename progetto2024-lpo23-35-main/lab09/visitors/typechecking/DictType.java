package lab09.visitors.typechecking;

import static java.util.Objects.requireNonNull;

import lab09.parser.ast.IntLiteral;


public record DictType(Type keyType, Type valueType) implements Type { 
	public static final String TYPE_NAME = "DICT";
	public DictType {
		requireNonNull(keyType);
		requireNonNull(valueType);
	}

	@Override
	public String toString() { 
		return String.format("%s %s",valueType.toString(), TYPE_NAME);
	}

}
