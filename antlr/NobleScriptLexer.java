// Generated from //wsl$/Ubuntu-20.04/home/ciupam/repos/noble-script/src\NobleScript.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NobleScriptLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, READ_DOUBLE=4, READ_INT=5, RETURN=6, WHILE=7, 
		IF=8, ELIF=9, ELSE=10, PAR_OPEN=11, PAR_CLOSE=12, BRACES_OPEN=13, BRACES_CLOSE=14, 
		BRACKET_OPEN=15, BRACKET_CLOSE=16, NULL=17, INT_LITERAL=18, DOUBLE_LITERAL=19, 
		BOOLEAN_LITERAL=20, STRING_LITERAL=21, BOOLEAN_TYPE=22, INT_TYPE=23, DOUBLE_TYPE=24, 
		STRING_TYPE=25, VAR_TYPE=26, ID=27, SEMICOL=28, ASSIGN_OP=29, LESSER_THAN_OP=30, 
		LESSER_THAN_OR_EQUAL_OP=31, GREATER_THAN_OP=32, GREATER_THAN_OR_EQUAL_OP=33, 
		EQUAL_OP=34, NOT_EQUAL_OP=35, PLUS_OP=36, MINUS_OP=37, POW_OP=38, DIV_OP=39, 
		MUL_OP=40, WHITESPACE=41, NEWLINE=42;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "READ_DOUBLE", "READ_INT", "RETURN", "WHILE", 
			"IF", "ELIF", "ELSE", "PAR_OPEN", "PAR_CLOSE", "BRACES_OPEN", "BRACES_CLOSE", 
			"BRACKET_OPEN", "BRACKET_CLOSE", "NULL", "INT_LITERAL", "DOUBLE_LITERAL", 
			"BOOLEAN_LITERAL", "STRING_LITERAL", "BOOLEAN_TYPE", "INT_TYPE", "DOUBLE_TYPE", 
			"STRING_TYPE", "VAR_TYPE", "ID", "SEMICOL", "ASSIGN_OP", "LESSER_THAN_OP", 
			"LESSER_THAN_OR_EQUAL_OP", "GREATER_THAN_OP", "GREATER_THAN_OR_EQUAL_OP", 
			"EQUAL_OP", "NOT_EQUAL_OP", "PLUS_OP", "MINUS_OP", "POW_OP", "DIV_OP", 
			"MUL_OP", "WHITESPACE", "NEWLINE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'struct'", "','", "'print('", "'readDouble()'", "'readInt()'", 
			"'return'", "'while'", "'if'", "'elif'", "'else'", "'('", "')'", "'{'", 
			"'}'", "'['", "']'", "'null'", null, null, null, null, "'boolean'", "'int'", 
			"'double'", "'string'", "'var'", null, "';'", "'='", "'<'", "'<='", "'>'", 
			"'>='", "'=='", "'!='", "'+'", "'-'", "'^'", "'/'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "READ_DOUBLE", "READ_INT", "RETURN", "WHILE", 
			"IF", "ELIF", "ELSE", "PAR_OPEN", "PAR_CLOSE", "BRACES_OPEN", "BRACES_CLOSE", 
			"BRACKET_OPEN", "BRACKET_CLOSE", "NULL", "INT_LITERAL", "DOUBLE_LITERAL", 
			"BOOLEAN_LITERAL", "STRING_LITERAL", "BOOLEAN_TYPE", "INT_TYPE", "DOUBLE_TYPE", 
			"STRING_TYPE", "VAR_TYPE", "ID", "SEMICOL", "ASSIGN_OP", "LESSER_THAN_OP", 
			"LESSER_THAN_OR_EQUAL_OP", "GREATER_THAN_OP", "GREATER_THAN_OR_EQUAL_OP", 
			"EQUAL_OP", "NOT_EQUAL_OP", "PLUS_OP", "MINUS_OP", "POW_OP", "DIV_OP", 
			"MUL_OP", "WHITESPACE", "NEWLINE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public NobleScriptLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "NobleScript.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2,\u013a\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r"+
		"\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\5\23\u00ab\n\23\3\23\6\23\u00ae\n\23\r\23\16\23\u00af\3\23\7\23\u00b3"+
		"\n\23\f\23\16\23\u00b6\13\23\3\23\5\23\u00b9\n\23\3\24\5\24\u00bc\n\24"+
		"\3\24\6\24\u00bf\n\24\r\24\16\24\u00c0\3\24\7\24\u00c4\n\24\f\24\16\24"+
		"\u00c7\13\24\3\24\3\24\6\24\u00cb\n\24\r\24\16\24\u00cc\3\24\5\24\u00d0"+
		"\n\24\3\24\3\24\3\24\3\24\6\24\u00d6\n\24\r\24\16\24\u00d7\5\24\u00da"+
		"\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u00e5\n\25\3\26"+
		"\3\26\7\26\u00e9\n\26\f\26\16\26\u00ec\13\26\3\26\3\26\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34"+
		"\3\34\7\34\u0110\n\34\f\34\16\34\u0113\13\34\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3 \3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3&\3&\3\'\3\'"+
		"\3(\3(\3)\3)\3*\3*\3*\3*\3+\3+\3+\3+\2\2,\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+"+
		"U,\3\2\t\3\2\63;\3\2\62;\3\2$$\4\2C\\c|\7\2//\62;C\\aac|\4\2\13\13\"\""+
		"\4\2\f\f\17\17\2\u0147\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2"+
		"Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\3W\3\2\2\2\5^\3\2\2\2\7`\3\2\2\2\tg\3"+
		"\2\2\2\13t\3\2\2\2\r~\3\2\2\2\17\u0085\3\2\2\2\21\u008b\3\2\2\2\23\u008e"+
		"\3\2\2\2\25\u0093\3\2\2\2\27\u0098\3\2\2\2\31\u009a\3\2\2\2\33\u009c\3"+
		"\2\2\2\35\u009e\3\2\2\2\37\u00a0\3\2\2\2!\u00a2\3\2\2\2#\u00a4\3\2\2\2"+
		"%\u00b8\3\2\2\2\'\u00d9\3\2\2\2)\u00e4\3\2\2\2+\u00e6\3\2\2\2-\u00ef\3"+
		"\2\2\2/\u00f7\3\2\2\2\61\u00fb\3\2\2\2\63\u0102\3\2\2\2\65\u0109\3\2\2"+
		"\2\67\u010d\3\2\2\29\u0114\3\2\2\2;\u0116\3\2\2\2=\u0118\3\2\2\2?\u011a"+
		"\3\2\2\2A\u011d\3\2\2\2C\u011f\3\2\2\2E\u0122\3\2\2\2G\u0125\3\2\2\2I"+
		"\u0128\3\2\2\2K\u012a\3\2\2\2M\u012c\3\2\2\2O\u012e\3\2\2\2Q\u0130\3\2"+
		"\2\2S\u0132\3\2\2\2U\u0136\3\2\2\2WX\7u\2\2XY\7v\2\2YZ\7t\2\2Z[\7w\2\2"+
		"[\\\7e\2\2\\]\7v\2\2]\4\3\2\2\2^_\7.\2\2_\6\3\2\2\2`a\7r\2\2ab\7t\2\2"+
		"bc\7k\2\2cd\7p\2\2de\7v\2\2ef\7*\2\2f\b\3\2\2\2gh\7t\2\2hi\7g\2\2ij\7"+
		"c\2\2jk\7f\2\2kl\7F\2\2lm\7q\2\2mn\7w\2\2no\7d\2\2op\7n\2\2pq\7g\2\2q"+
		"r\7*\2\2rs\7+\2\2s\n\3\2\2\2tu\7t\2\2uv\7g\2\2vw\7c\2\2wx\7f\2\2xy\7K"+
		"\2\2yz\7p\2\2z{\7v\2\2{|\7*\2\2|}\7+\2\2}\f\3\2\2\2~\177\7t\2\2\177\u0080"+
		"\7g\2\2\u0080\u0081\7v\2\2\u0081\u0082\7w\2\2\u0082\u0083\7t\2\2\u0083"+
		"\u0084\7p\2\2\u0084\16\3\2\2\2\u0085\u0086\7y\2\2\u0086\u0087\7j\2\2\u0087"+
		"\u0088\7k\2\2\u0088\u0089\7n\2\2\u0089\u008a\7g\2\2\u008a\20\3\2\2\2\u008b"+
		"\u008c\7k\2\2\u008c\u008d\7h\2\2\u008d\22\3\2\2\2\u008e\u008f\7g\2\2\u008f"+
		"\u0090\7n\2\2\u0090\u0091\7k\2\2\u0091\u0092\7h\2\2\u0092\24\3\2\2\2\u0093"+
		"\u0094\7g\2\2\u0094\u0095\7n\2\2\u0095\u0096\7u\2\2\u0096\u0097\7g\2\2"+
		"\u0097\26\3\2\2\2\u0098\u0099\7*\2\2\u0099\30\3\2\2\2\u009a\u009b\7+\2"+
		"\2\u009b\32\3\2\2\2\u009c\u009d\7}\2\2\u009d\34\3\2\2\2\u009e\u009f\7"+
		"\177\2\2\u009f\36\3\2\2\2\u00a0\u00a1\7]\2\2\u00a1 \3\2\2\2\u00a2\u00a3"+
		"\7_\2\2\u00a3\"\3\2\2\2\u00a4\u00a5\7p\2\2\u00a5\u00a6\7w\2\2\u00a6\u00a7"+
		"\7n\2\2\u00a7\u00a8\7n\2\2\u00a8$\3\2\2\2\u00a9\u00ab\7/\2\2\u00aa\u00a9"+
		"\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00ae\t\2\2\2\u00ad"+
		"\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2"+
		"\2\2\u00b0\u00b4\3\2\2\2\u00b1\u00b3\t\3\2\2\u00b2\u00b1\3\2\2\2\u00b3"+
		"\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b9\3\2"+
		"\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00b9\7\62\2\2\u00b8\u00aa\3\2\2\2\u00b8"+
		"\u00b7\3\2\2\2\u00b9&\3\2\2\2\u00ba\u00bc\7/\2\2\u00bb\u00ba\3\2\2\2\u00bb"+
		"\u00bc\3\2\2\2\u00bc\u00be\3\2\2\2\u00bd\u00bf\t\2\2\2\u00be\u00bd\3\2"+
		"\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00c5\3\2\2\2\u00c2\u00c4\t\3\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c7\3\2"+
		"\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c8\3\2\2\2\u00c7"+
		"\u00c5\3\2\2\2\u00c8\u00ca\7\60\2\2\u00c9\u00cb\t\3\2\2\u00ca\u00c9\3"+
		"\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd"+
		"\u00da\3\2\2\2\u00ce\u00d0\7/\2\2\u00cf\u00ce\3\2\2\2\u00cf\u00d0\3\2"+
		"\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d2\7\62\2\2\u00d2\u00d3\7\60\2\2\u00d3"+
		"\u00d5\3\2\2\2\u00d4\u00d6\t\3\2\2\u00d5\u00d4\3\2\2\2\u00d6\u00d7\3\2"+
		"\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00da\3\2\2\2\u00d9"+
		"\u00bb\3\2\2\2\u00d9\u00cf\3\2\2\2\u00da(\3\2\2\2\u00db\u00dc\7v\2\2\u00dc"+
		"\u00dd\7t\2\2\u00dd\u00de\7w\2\2\u00de\u00e5\7g\2\2\u00df\u00e0\7h\2\2"+
		"\u00e0\u00e1\7c\2\2\u00e1\u00e2\7n\2\2\u00e2\u00e3\7u\2\2\u00e3\u00e5"+
		"\7g\2\2\u00e4\u00db\3\2\2\2\u00e4\u00df\3\2\2\2\u00e5*\3\2\2\2\u00e6\u00ea"+
		"\7$\2\2\u00e7\u00e9\n\4\2\2\u00e8\u00e7\3\2\2\2\u00e9\u00ec\3\2\2\2\u00ea"+
		"\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ed\3\2\2\2\u00ec\u00ea\3\2"+
		"\2\2\u00ed\u00ee\7$\2\2\u00ee,\3\2\2\2\u00ef\u00f0\7d\2\2\u00f0\u00f1"+
		"\7q\2\2\u00f1\u00f2\7q\2\2\u00f2\u00f3\7n\2\2\u00f3\u00f4\7g\2\2\u00f4"+
		"\u00f5\7c\2\2\u00f5\u00f6\7p\2\2\u00f6.\3\2\2\2\u00f7\u00f8\7k\2\2\u00f8"+
		"\u00f9\7p\2\2\u00f9\u00fa\7v\2\2\u00fa\60\3\2\2\2\u00fb\u00fc\7f\2\2\u00fc"+
		"\u00fd\7q\2\2\u00fd\u00fe\7w\2\2\u00fe\u00ff\7d\2\2\u00ff\u0100\7n\2\2"+
		"\u0100\u0101\7g\2\2\u0101\62\3\2\2\2\u0102\u0103\7u\2\2\u0103\u0104\7"+
		"v\2\2\u0104\u0105\7t\2\2\u0105\u0106\7k\2\2\u0106\u0107\7p\2\2\u0107\u0108"+
		"\7i\2\2\u0108\64\3\2\2\2\u0109\u010a\7x\2\2\u010a\u010b\7c\2\2\u010b\u010c"+
		"\7t\2\2\u010c\66\3\2\2\2\u010d\u0111\t\5\2\2\u010e\u0110\t\6\2\2\u010f"+
		"\u010e\3\2\2\2\u0110\u0113\3\2\2\2\u0111\u010f\3\2\2\2\u0111\u0112\3\2"+
		"\2\2\u01128\3\2\2\2\u0113\u0111\3\2\2\2\u0114\u0115\7=\2\2\u0115:\3\2"+
		"\2\2\u0116\u0117\7?\2\2\u0117<\3\2\2\2\u0118\u0119\7>\2\2\u0119>\3\2\2"+
		"\2\u011a\u011b\7>\2\2\u011b\u011c\7?\2\2\u011c@\3\2\2\2\u011d\u011e\7"+
		"@\2\2\u011eB\3\2\2\2\u011f\u0120\7@\2\2\u0120\u0121\7?\2\2\u0121D\3\2"+
		"\2\2\u0122\u0123\7?\2\2\u0123\u0124\7?\2\2\u0124F\3\2\2\2\u0125\u0126"+
		"\7#\2\2\u0126\u0127\7?\2\2\u0127H\3\2\2\2\u0128\u0129\7-\2\2\u0129J\3"+
		"\2\2\2\u012a\u012b\7/\2\2\u012bL\3\2\2\2\u012c\u012d\7`\2\2\u012dN\3\2"+
		"\2\2\u012e\u012f\7\61\2\2\u012fP\3\2\2\2\u0130\u0131\7,\2\2\u0131R\3\2"+
		"\2\2\u0132\u0133\t\7\2\2\u0133\u0134\3\2\2\2\u0134\u0135\b*\2\2\u0135"+
		"T\3\2\2\2\u0136\u0137\t\b\2\2\u0137\u0138\3\2\2\2\u0138\u0139\b+\2\2\u0139"+
		"V\3\2\2\2\21\2\u00aa\u00af\u00b4\u00b8\u00bb\u00c0\u00c5\u00cc\u00cf\u00d7"+
		"\u00d9\u00e4\u00ea\u0111\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}