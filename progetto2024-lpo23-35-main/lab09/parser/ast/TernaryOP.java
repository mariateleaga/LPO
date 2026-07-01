package lab09.parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class TernaryOP implements Exp {
	protected final Exp first;
	protected final Exp second;
	protected final Exp third;

	protected TernaryOP(Exp first, Exp second, Exp third) {
		this.first = requireNonNull(first);
		this.second = requireNonNull(second);
		this.third = requireNonNull(third);
	}

	@Override
	public String toString() {
		return String.format("%s(%s,%s,%s)", getClass().getSimpleName(), first, second, third);
	}

}
