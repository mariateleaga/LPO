package lab09.visitors.execution;

import java.io.PrintWriter;

import lab09.environments.EnvironmentException;
import lab09.environments.GenEnvironment;
import lab09.parser.ast.Block;
import lab09.parser.ast.Exp;
import lab09.parser.ast.NamedEntity;
import lab09.parser.ast.Stmt;
import lab09.parser.ast.StmtSeq;
import lab09.parser.ast.Variable;
import lab09.visitors.Visitor;
import lab09.visitors.typechecking.DictType;
import lab09.visitors.typechecking.PairType;
import java.util.TreeMap;

import static java.util.Objects.requireNonNull;
import static lab09.visitors.typechecking.AtomicType.INT;

public class Execute implements Visitor<Value> {

	private final DynamicEnv env = new DynamicEnv();
	private final PrintWriter printWriter; // output stream used to print values

	public Execute() {
		printWriter = new PrintWriter(System.out, true);
	}

	public Execute(PrintWriter printWriter) {
		this.printWriter = requireNonNull(printWriter);
	}

	// dynamic semantics for programs; no value returned by the visitor

	@Override
	public Value visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
			// possible runtime errors
			// EnvironmentException: undefined variable
		} catch (EnvironmentException e) {
			throw new InterpreterException(e);
		}
		return null;
	}

	// dynamic semantics for statements; no value returned by the visitor
	
	// | AssignStmt(var, exp) -> update var (evalExp env exp) env
	@Override
	public Value visitAssignStmt(Variable var, Exp exp) {
		env.update(var, exp.accept(this));
		return null;
	    // COMPLETATO
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		printWriter.println(exp.accept(this));
		return null;
	}

	// | VarStmt(var, exp) -> dec var (evalExp env exp) env
	@Override
	public Value visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
	    // COMPLETATO
	}

	/* 
	| IfStmt(exp, thenBlock, elseBlock) ->
        if toBool (evalExp env exp) then
            executeBlock env thenBlock
        else
            executeBlock env elseBlock */
	@Override
	public Value visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		if((exp.accept(this)).toBool())
			thenBlock.accept(this);
		else if (elseBlock != null)
			elseBlock.accept(this);
		return null;
	    // COMPLETATO
	}
	
	@Override
	public Value visitForStmt(Variable var, Exp exp, Block forBlock) {
		DictValue m = exp.accept(this).toMap();
		env.enterScope();
		env.dec(var,new IntValue(0));
		for (int key : m.getMap().keySet()){ 
			Value val = (m.getMap()).get(key);
			env.update(var, new PairValue(new IntValue(key), val));

			forBlock.accept(this);		
		}
		env.exitScope();
		return null;
		//COMPLETATO
	}

	//| EmptyBlock -> env
    //| Block stmtSeq -> executeStmtSeq (enterScope env) stmtSeq |> exitScope
	@Override
	public Value visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null;
		// COMPLETATO
	}

	// dynamic semantics for sequences of statements
	// no value returned by the visitor

	// | EmptyStmtSeq -> env
	@Override
	public Value visitEmptyStmtSeq() {
		return null;
	    // completata
	}

	//  | NonEmptyStmtSeq(stmt, stmtSeq) -> executeStmtSeq (executeStmt env stmt) stmtSeq
	@Override
	public Value visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	    // completata
	}

	// dynamic semantics of expressions; a value is returned by the visitor

	@Override
	public IntValue visitAdd(Exp left, Exp right) {
		return new IntValue(left.accept(this).toInt() + right.accept(this).toInt());
	}

	@Override
	public IntValue visitIntLiteral(int value) {
		return new IntValue(value);
	    // COMPLETATO
	}

    @Override
	public IntValue visitMul(Exp left, Exp right) {
		return new IntValue(left.accept(this).toInt() * right.accept(this).toInt());
	    // COMPLETATO
	}
    
	// | Sign exp -> evalExp env exp |> toInt |> (~-) |> IntValue // (~-) is the unary minus
	@Override
	public IntValue visitSign(Exp exp) {
		return new IntValue(-exp.accept(this).toInt());
	    // COMPLETATO
	}

	// | Var var -> lookup var env
	@Override
	public Value visitVariable(Variable var) {
		return env.lookup(var);
	    // COMPLETATO
	}

	@Override
	public BoolValue visitNot(Exp exp) {
		return new BoolValue(!exp.accept(this).toBool());
	}

	//  | And(exp1, exp2) -> ((evalExp env exp1 |> toBool) && (evalExp env exp2 |> toBool)) |> BoolValue
	@Override
	public BoolValue visitAnd(Exp left, Exp right) {
		return new BoolValue (left.accept(this).toBool()&&(right.accept(this).toBool()));
	    // COMPLETATO
	}

	@Override
	public BoolValue visitBoolLiteral(boolean value) {
		return new BoolValue(value);
	    // COMPLETATO
	}

	// | Eq(exp1, exp2) -> evalExp env exp1 = evalExp env exp2 |> BoolValue
	@Override
	public BoolValue visitEq(Exp left, Exp right) {
		return new BoolValue (left.accept(this).equals(right.accept(this)));
	    // COMPLETATO
	}

	// | PairLiteral(exp1, exp2) -> (evalExp env exp1, evalExp env exp2) |> PairValue
	@Override
	public PairValue visitPairLit(Exp left, Exp right) {
		return new PairValue(left.accept(this),right.accept(this));
	    // COMPLETATO
	}

	// | Fst exp -> evalExp env exp |> toPair |> fst
	@Override
	public Value visitFst(Exp exp) {
		return exp.accept(this).toPair().getFstVal();
	    // COMPLETATO
	}

	// | Snd exp -> evalExp env exp |> toPair |> snd
	@Override
	public Value visitSnd(Exp exp) {
		return exp.accept(this).toPair().getSndVal();
	    // COMPLETATO
	}


	
	@Override
	public DictValue visitDictCons (Exp key, Exp value){
		return new DictValue((key.accept(this)).toInt(), value.accept(this));
	}

	@Override
	public DictValue visitDictDel (Exp dict, Exp key){
		DictValue d = (dict.accept(this)).toMap();
		d.remove((key.accept(this)).toInt());
		return d;

	}
	
	@Override
	public Value visitDictGet (Exp dict, Exp key){
		DictValue d = (dict.accept(this)).toMap();
		return d.get((key.accept(this)).toInt());
	}

	@Override
	public DictValue visitDictPut (Exp dict, Exp key, Exp value){
		DictValue d = (dict.accept(this)).toMap();
		return d.put(d,(key.accept(this)).toInt(), value.accept(this));

	}

}

