package interpreter.symbol;

import java.util.Vector;

public class SymbolTable {
	/* 存放SymbolTableElement */
	private Vector<SymbolTableElement> symbolTable = new Vector<SymbolTableElement>();


	public SymbolTableElement get(int index) {
		return symbolTable.get(index);
	}


	public SymbolTableElement getAllLevel(String name, int level) {
		while (level > -1) {
			for (SymbolTableElement element : symbolTable) {
				if (element.getName().equals(name)
						&& element.getLevel() == level) {
					return element;
				}
			}
			level--;
		}
		return null;
	}


	public SymbolTableElement getCurrentLevel(String name, int level) {
		for (SymbolTableElement element : symbolTable) {
			if (element.getName().equals(name) && element.getLevel() == level) {
				return element;
			}
		}
		return null;
	}


	public boolean add(SymbolTableElement element) {
		return symbolTable.add(element);
	}


	public void add(int index, SymbolTableElement element) {
		symbolTable.add(index, element);
	}


	public void remove(int index) {
		symbolTable.remove(index);
	}


	public void remove(String name, int level) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getName().equals(name) && get(i).getLevel() == level) {
				remove(i);
				return;
			}
		}
	}


	public void removeAll() {
		symbolTable.clear();
	}


	public void update(int level) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getLevel() > level) {
				remove(i);
			}
		}
	}


	public boolean contains(SymbolTableElement element) {
		return symbolTable.contains(element);
	}


	public boolean isEmpty() {
		return symbolTable.isEmpty();
	}


	public int size() {
		return symbolTable.size();
	}

}
