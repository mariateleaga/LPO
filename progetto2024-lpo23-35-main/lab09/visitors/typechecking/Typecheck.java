package lab09.visitors.typechecking;

import static lab09.visitors.typechecking.AtomicType.*;

import java.security.Key;

import com.sun.jdi.IntegerType;
import lab09.environments.EnvironmentException;
import lab09.environments.GenEnvironment;
import lab09.parser.ast.*;
import lab09.visitors.Visitor;

public class Typecheck implements Visitor<Type> {

	private final StaticEnv env = new StaticEnv();

    // useful to typecheck binary operations where operands must have the same type 
	private void checkBinOp(Exp left, Exp right, Type type) {
		type.checkEqual(left.accept(this));
		type.checkEqual(right.accept(this));
	}

	// static semantics for programs; no value returned by the visitor

	@Override
	public Type visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
		} catch (EnvironmentException e) { // undeclared variable
			throw new TypecheckerException(e);
		}
		return null;
	}

	// static semantics for statements; no value returned by the visitor

	@Override
	public Type visitAssignStmt(Variable var, Exp exp) {
		Type type1 = env.lookup(var);
		checkBinOp(var, exp, type1);
		return null;
		//COMPLETATO
	}

	@Override
	public Type visitPrintStmt(Exp exp) {
		exp.accept(this);
		return null;
	}

	@Override
	public Type visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
		//COMPLETATO
	}

	@Override
	public Type visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		BOOL.checkEqual(exp.accept(this));
		thenBlock.accept(this);
		if (elseBlock != null)
			elseBlock.accept(this);
		return null;
		// COMPLETATO
	}

	@Override
	public Type visitForStmt(Variable var, Exp exp, Block forBlock) {
		DictType dictType = exp.accept(this).checkIsDictType();
		env.enterScope();
		PairType varType = new PairType(INT,dictType.getValueType());
		env.dec(var, varType);
		forBlock.accept(this);
		env.exitScope();
		return null;
		//COMPLETATO
	}


	@Override
	public Type visitDictGet(Exp dict, Exp key) {
		dict.accept(this).checkIsDictType();
		INT.checkEqual(key.accept(this));
		return dict.accept(this).getValueType();
		//COMPLETATO
	}

	@Override
	public Type visitDictDel(Exp dict, Exp key) { 
		dict.accept(this).checkIsDictType(); 
		INT.checkEqual(key.accept(this));
		return dict.accept(this);
		//COMPLETATO
	}

	@Override
	public DictType visitDictCons(Exp key, Exp value) {
		INT.checkEqual(key.accept(this));
		return new DictType(key.accept(this),value.accept(this));
		//COMPLETATO
	}

	@Override
	public Type visitDictPut(Exp dict, Exp key, Exp value) {
		dict.accept(this).checkIsDictType();
		Type valueType = dict.accept(this).getValueType();
		valueType.checkEqual(value.accept(this));
		key.accept(this).checkEqual(INT);
		return dict.accept(this);
		//COMPLETATO
	}

	@Override
	public Type visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null; 
		//COMPLETATO
	}

	// static semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Type visitEmptyStmtSeq() {
		return null;
	    //COMPLETATO
	}

	@Override
	public Type visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
		//COMPLETATO
	}

	// static semantics of expressions; a type is returned by the visitor

	@Override
	public AtomicType visitAdd(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public AtomicType visitIntLiteral(int value) {
		return INT;
	    //COMPLETATO
	}

	@Override
	public AtomicType visitMul(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	    //COMPLETATO
	}

	@Override
	public AtomicType visitSign(Exp exp) {
		INT.checkEqual(exp.accept(this));
		return INT;
	    //COMPLETATO
	}

	@Override
	public Type visitVariable(Variable var) {
		return env.lookup(var);
	    //COMPLETATO
	}

	@Override
	public AtomicType visitNot(Exp exp) {
		BOOL.checkEqual(exp.accept(this));
		return BOOL;
	}

	@Override
	public AtomicType visitAnd(Exp left, Exp right) {
		checkBinOp(left, right, BOOL);
		return BOOL;
	    //COMPLETATO
	}

	@Override
	public AtomicType visitBoolLiteral(boolean value) {
		return BOOL;
	    //COMPLETATO
	}

	@Override
	public AtomicType visitEq(Exp left, Exp right) {
		left.accept(this).checkEqual(right.accept(this));
		return BOOL;
	    //COMPLETATO
	}

	@Override
	public PairType visitPairLit(Exp left, Exp right) {
		return new PairType(left.accept(this),right.accept(this));
		//COMPLETATO
	}

	@Override
	public Type visitFst(Exp exp) {
		return exp.accept(this).getFstPairType();
		//COMPLETATO
	}

	@Override
	public Type visitSnd(Exp exp) {
		return exp.accept(this).getSndPairType();
		//COMPLETATO
	}

}
