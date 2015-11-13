/*
Classe Antiga minha que faz uma tabela bem completa
 * XprisTableModel.java
 *
 * Created on 27 de Junho de 2005, 12:35
 */
package bugapp.gui;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
/**
 *
 * @author  Henrique
 */
public class XprisTable extends javax.swing.JTable{
    
    protected Vector<String> VisibleTitle;
    protected Vector<Vector> VisibleData;
    
    protected Vector<String> CompleteTitle;
    protected Vector<Vector> CompleteData;
    
    protected XprisTableModel tmdModelo;
    protected XprisTableCellRenderer tcrCellRenderer;
    
    protected int[] CompleteColSizes;
    protected boolean[] CompleteColEditable;
    protected Class[] CompleteColTypes;
    
    protected Vector<Boolean> RowEditable;
    
    public static int CELULA_ALINHADO_CENTRO = 0x03000000;
    public static int CELULA_ALINHADO_ESQUERDA = 0x01000000;
    public static int CELULA_ALINHADO_DIREITA = 0x02000000;
    
    public boolean AlternateBackgroundColor=false;
    
    protected int LastSelectedRow = -1;
    protected int LastSelectedCol = -1;
    
    /** Creates a new instance of XprisTable */
    public XprisTable() {
        super();
        
        VisibleData=new Vector<Vector>();
        VisibleTitle=new Vector<String>();
        tmdModelo=new XprisTableModel();
        tcrCellRenderer=new XprisTableCellRenderer();
        
        CompleteTitle=new Vector<String>();
        CompleteData=new Vector<Vector>();
        RowEditable=new Vector<Boolean>();
        
        this.setModel(tmdModelo);
        tmdModelo.setDataVector(VisibleData,VisibleTitle);
        this.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        this.setDefaultRenderer(java.lang.String.class,tcrCellRenderer);
        //this.setDefaultRenderer(Float.class,new XprisDinheiroCellRenderer());
        this.setDefaultEditor(Double.class,new XprisDoubleCellEditor());
        //this.setDefaultEditor(Float.class,new XprisDinheiroCellEditor());
        this.getTableHeader().setReorderingAllowed(false);
        
        tmdModelo.addTableModelListener(new javax.swing.event.TableModelListener(){
            public void tableChanged(javax.swing.event.TableModelEvent e){
                if(e.getType()==e.UPDATE){
                    int Col=e.getColumn();
                    int Row=e.getLastRow();
                    if(Col>=0 && Row >=0)
                        setVisibleValue(Row,Col,tmdModelo.getValueAt(Row,Col));
                }
            }
        });
        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(evt.getClickCount()<2)
                    XprisTableMouseClicked();
            }
        });
        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }
    
    /**
     * Inicializa as variavéis sistemáticas do XprisTable
     * NÃO UTILIZAR. Esse método é chamado automaticamente.
     */
    protected void initVariables(){
        if(CompleteColSizes==null){
            CompleteColSizes=new int[CompleteTitle.size()];
            java.util.Arrays.fill(CompleteColSizes,-1);
        }
        
        if(CompleteColEditable==null){
            CompleteColEditable=new boolean[CompleteTitle.size()];
            java.util.Arrays.fill(CompleteColEditable,false);
        }
        
        if(CompleteColTypes==null){
            CompleteColTypes=new Class[CompleteTitle.size()];
            java.util.Arrays.fill(CompleteColTypes,java.lang.String.class);
        }
    }
    
    /**
     * Seta os Títulos de TODAS as colunas do XprisTable
     *
     * @param newTitle Vetor contendo os títulos das colunas
     */
    public void setTitle(String[] newTitle){
        CompleteTitle.clear();
        VisibleTitle.clear();
        for(int i=0; i<newTitle.length; i++){
            CompleteTitle.add(newTitle[i]);
            VisibleTitle.add(newTitle[i]);
        }
        
        tmdModelo.setDataVector(VisibleData,VisibleTitle);
        initVariables();
        setColumnSize();
    }
    
    /**
     * Seta o título de uma coluna do XprisTable
     *
     * @param ColIndex Índice da coluna baseado nos dados completos.
     * @param newTitle O novo título da coluna
     */
    public void setTitle(int ColIndex, String newTitle){
        VisibleTitle.set(getVisibleColumnIndex(ColIndex),newTitle);
        CompleteTitle.set(ColIndex,newTitle);
        tmdModelo.setDataVector(VisibleData,VisibleTitle);
        setColumnSize();
    }
    
    /**
     * Seta o tamanho em pixels de uma coluna do XprisTable
     * @param ColIndex Índice da coluna baseado nos dados completos.
     * @param Size Tamanho em pixels da coluna
     */
    public void setColumnSize(int ColIndex, int Size){
        CompleteColSizes[ColIndex]=Size;
        javax.swing.table.TableColumn Coluna = this.getColumnModel().getColumn(ColIndex);
        Coluna.setPreferredWidth(Size);
    }
    
    /**
     * Seta os tamanhos em pixels de TODAS as colunas do XprisTable
     */
    public void setColumnSize(int[] Sizes){
        CompleteColSizes=Sizes;
        setColumnSize();
    }
    
    /**
     * Método protegido que pega cada <code>TableColumn</code> e altera seu preferred width.
     */
    protected void setColumnSize(){
        if(CompleteColSizes!=null){
            javax.swing.table.TableColumn Coluna = null;
            for(int i=0, j=0; i<CompleteTitle.size(); i++){
                
                if(j<VisibleTitle.size() && CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(j))){
                    Coluna = this.getColumnModel().getColumn(j);
                    Coluna.setWidth(CompleteColSizes[i]);
                    Coluna.setPreferredWidth(CompleteColSizes[i]);
                    //Coluna.setCellRenderer(tcrCellRenderer);
                    j++;
                }
            }
        }
    }
    
    /**
     * Indica se a coluna poderá ser editada pelo usuário. Valor default false.
     * @param ColIndex Índice da coluna baseado nos dados completos.
     * @param aFlag True se o usuário puder editar a coluna, False caso o contrário.
     */
    public void setColumnEditable(int ColIndex, boolean aFlag){
        CompleteColEditable[ColIndex]=aFlag;
    }
    
    /**
     * Seta se o usuário poderá editar cada uma das colunas do XprisTable.
     */
    public void setColumnEditable(boolean[] Flags){
        CompleteColEditable=Flags;
    }
    
    public void setRowEditable(int Row, boolean Flag){
        RowEditable.set(Row,new Boolean(Flag));
    }
    
    public boolean isRowEditable(int Row){
        return RowEditable.get(Row).booleanValue();
    }
    
    /**
     * Seta a Classe de uma coluna do XprisTable. Valor default <code>String.class</code>.
     */
    public void setColumnClass(int ColIndex, Class Classe){
        CompleteColTypes[ColIndex]=Classe;
    }
    
    /**
     * Seta as Classes de TODAS as colunas do XprisTable.
     */
    public void setColumnClass(Class[] Classes){
        CompleteColTypes=Classes;
    }
    
    public int getColumnIndex(String ColName){
        int ColIndex=-1;
        for(int i=0; i<CompleteTitle.size(); i++){
            if(CompleteTitle.get(i).equalsIgnoreCase(ColName)){
                ColIndex=i;
                break;
            }
        }
        return ColIndex;
    }
    
    public int getVisibleColumnIndex(String ColName){
        int ColIndex=-1;
        for(int i=0; i<VisibleTitle.size(); i++){
            if(VisibleTitle.get(i).equalsIgnoreCase(ColName)){
                ColIndex=i;
                break;
            }
        }
        return ColIndex;
    }
    
    public int getVisibleColumnIndex(int CompleteIndex){
        int ColIndex=-1;
        String ColName=CompleteTitle.get(CompleteIndex);
        for(int i=0; i<VisibleTitle.size(); i++){
            if(VisibleTitle.get(i).equalsIgnoreCase(ColName)){
                ColIndex=i;
                break;
            }
        }
        return ColIndex;
    }
    
    public int getCompleteColumnIndex(int VisibleIndex){
        int ColIndex=-1;
        String ColName=VisibleTitle.get(VisibleIndex);
        for(int i=0; i<CompleteTitle.size(); i++){
            if(CompleteTitle.get(i).equalsIgnoreCase(ColName)){
                ColIndex=i;
                break;
            }
        }
        return ColIndex;
    }
    
    public int getCompleteColumnIndex(String ColName){
        int ColIndex=-1;
        for(int i=0; i<CompleteTitle.size(); i++)
            if(CompleteTitle.get(i).equalsIgnoreCase(ColName)){
            ColIndex=i;
            break;
            }
        return ColIndex;
    }
    
    public int getCompleteColumnCount(){
        return CompleteTitle.size();
    }
    
    /**
     * Retorna o objeto na posição especificada, mesmo que esteja invisivel.
     *
     * ATENÇÃO: Se estiver usando o método pra exibir colunas utilizar o metodo
     * get(int row, String colName) para ter certeza que o dado retornado será o correto.
     *
     * @return O objeto na posição indicada
     */
    public Object get(int row, int col){
        return CompleteData.get(row).get(col);
    }
    
    public Object get(int row, String colName){
        int ColIndex=-1;
        for(int i=0; i<CompleteTitle.size(); i++){
            if(CompleteTitle.get(i).equalsIgnoreCase(colName)){
                ColIndex=i;
                break;
            }
        }
        
        if(ColIndex>=0)
            return this.get(row,ColIndex);
        else
            return null;
    }
    
    public Object getVisible(int row, int col){
        return VisibleData.get(row).get(col);
    }
    
    public Object getVisible(int row, String colName){
        int ColIndex=-1;
        for(int i=0; i<VisibleTitle.size(); i++){
            if(VisibleTitle.get(i).equalsIgnoreCase(colName)){
                ColIndex=i;
                break;
            }
        }
        
        if(ColIndex>=0)
            return this.getVisible(row,ColIndex);
        else
            return null;
    }
    
    public void setParameter(int Row, int Col, int Par){
        
        int VisCol=getVisibleColumnIndex(Col);
        if(VisCol>=0){
            String Cel[]=VisibleData.get(Row).get(VisCol).toString().split("#");
            VisibleData.get(Row).set(VisCol,Cel[0]+"#"+Integer.toString(Par));
        }
        tmdModelo.fireTableDataChanged();
    }
    
    public void set(int Row, int Col, Object value){
        
        int VisCol=getVisibleColumnIndex(Col);
        if(VisCol>=0)
            VisibleData.get(Row).set(VisCol,value);
        CompleteData.get(Row).set(Col,value);
        tmdModelo.fireTableDataChanged();
    }
    
    public void set(int Row, String ColName, Object value){
        int i;
        int CompleteColIndex=-1, VisibleColIndex=-1;
        
        for(i=0; i<CompleteTitle.size(); i++){
            if(CompleteTitle.get(i).equalsIgnoreCase(ColName)){
                CompleteColIndex=i;
                break;
            }
        }
        
        for(i=0; i<VisibleTitle.size(); i++){
            if(VisibleTitle.get(i).equalsIgnoreCase(ColName)){
                VisibleColIndex=i;
                break;
            }
        }
        
        if(CompleteColIndex>=0){
            CompleteData.get(Row).set(CompleteColIndex,value);
            if(VisibleColIndex>=0){
                VisibleData.get(Row).set(VisibleColIndex,value);
                tmdModelo.fireTableDataChanged();
            }
        }
    }
    
    public void setVisibleValue(int Row, int Col, Object Value){
        VisibleData.get(Row).set(Col,Value);
        
        int i, CompIndex=-1;
        //Encontrar o indice relativo ao dados completos
        for(i=0; i<CompleteTitle.size(); i++){
            if(CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(Col))){
                CompIndex=i;
                break;
            }
        }
        
        CompleteData.get(Row).set(CompIndex,Value);
        tmdModelo.fireTableDataChanged();
    }
    
    public void clearData() {
        CompleteData.clear();
        VisibleData.clear();
        tmdModelo.fireTableDataChanged();
        
        RowEditable.clear();
    }
    
    public void setData(java.sql.ResultSet Rs) throws java.sql.SQLException{
        int ColCount=Rs.getMetaData().getColumnCount();
        CompleteData.clear();
        VisibleData.clear();
        RowEditable.clear();
        
        while(Rs.next()){
            Vector<Object> CompleteLine=new Vector<Object>(ColCount);
            Vector<Object> VisibleLine=new Vector<Object>(ColCount);
            for(int i=0, j=0; i<ColCount; i++){
                Object dat=null;
                if(CompleteColTypes[i]==java.lang.Boolean.class)
                    dat=Rs.getBoolean(i+1);
                else if(CompleteColTypes[i]==java.lang.Integer.class || CompleteColTypes[i]==java.lang.Long.class)
                    dat=Rs.getLong(i+1);
                else if(CompleteColTypes[i]==java.lang.Double.class)
                    dat=Rs.getDouble(i+1);
                else if(CompleteColTypes[i]==java.lang.Float.class)
                    dat=Rs.getFloat(i+1);
                else if(CompleteColTypes[i]==java.sql.Date.class || CompleteColTypes[i]==java.util.Date.class)
                    dat=Rs.getDate(i+1);
                else{
                    dat=Rs.getString(i+1);
                    if(dat==null) dat="";
                }
                
                CompleteLine.add(dat);
                
                //Antes de adicionar aos dados visiveis é preciso verificar se a coluna é visivel
                if(j<VisibleTitle.size() && CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(j))){
                    VisibleLine.add(dat);
                    j++;
                }
            }
            CompleteLine.setSize(CompleteTitle.size());
            VisibleLine.setSize(VisibleTitle.size());
            
            CompleteData.add(CompleteLine);
            VisibleData.add(VisibleLine);
            RowEditable.add(Boolean.TRUE);
        }
        tmdModelo.fireTableDataChanged();
    }
    
    public void addRow(Vector<Object> CompleteRowData){
        Vector<Object> VisibleLine=new Vector<Object>();
        for(int i=0, j=0; i<CompleteTitle.size(); i++){
            
            //Antes de adicionar aos dados visiveis é preciso verificar se a coluna é visivel
            if(j<VisibleTitle.size() && CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(j))){
                VisibleLine.add(CompleteRowData.get(i));
                j++;
            }
        }
        CompleteData.add(CompleteRowData);
        VisibleData.add(VisibleLine);
        RowEditable.add(Boolean.TRUE);
        tmdModelo.fireTableDataChanged();
    }
    
    public void addRow(java.sql.ResultSet Rs) throws java.sql.SQLException{
        int ColCount=Rs.getMetaData().getColumnCount();
        
        while(Rs.next()){
            Vector<Object> CompleteLine=new Vector<Object>();
            Vector<Object> VisibleLine=new Vector<Object>();
            for(int i=0, j=0; i<ColCount && i<CompleteTitle.size(); i++){
                Object dat=null;
                if(CompleteColTypes[i]==java.lang.Boolean.class)
                    dat=Rs.getBoolean(i+1);
                else if(CompleteColTypes[i]==java.lang.Integer.class || CompleteColTypes[i]==java.lang.Long.class)
                    dat=Rs.getLong(i+1);
                else if(CompleteColTypes[i]==java.lang.Double.class)
                    dat=Rs.getDouble(i+1);
                else if(CompleteColTypes[i]==java.lang.Float.class)
                    dat=Rs.getFloat(i+1);
                else if(CompleteColTypes[i]==java.sql.Date.class || CompleteColTypes[i]==java.util.Date.class)
                    dat=Rs.getDate(i+1);
                else
                    dat=Rs.getString(i+1);
                
                CompleteLine.add(dat);
                
                //Antes de adicionar aos dados visiveis é preciso verificar se a coluna é visivel
                if(j<VisibleTitle.size() && CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(j))){
                    VisibleLine.add(dat);
                    j++;
                }
            }
            CompleteData.add(CompleteLine);
            VisibleData.add(VisibleLine);
            RowEditable.add(Boolean.TRUE);
        }
        tmdModelo.fireTableDataChanged();
    }
    
    public void addOrSelectRow(java.sql.ResultSet Rs) throws java.sql.SQLException{
        int ColCount=Rs.getMetaData().getColumnCount();
        int i, j, RowIndex=-1;
        
        if(Rs.next()){
            //Primeiro converter o resultset pra dados JTable
            Vector<Object> CompleteLine=new Vector<Object>();
            Vector<Object> VisibleLine=new Vector<Object>();
            for(i=0, j=0; i<ColCount; i++){
                Object dat=null;
                if(CompleteColTypes[i]==java.lang.Boolean.class)
                    dat=Rs.getBoolean(i+1);
                else if(CompleteColTypes[i]==java.lang.Long.class || CompleteColTypes[i]==java.lang.Integer.class)
                    dat=Rs.getLong(i+1);
                else if(CompleteColTypes[i]==java.lang.Double.class)
                    dat=Rs.getDouble(i+1);
                else if(CompleteColTypes[i]==java.lang.Float.class)
                    dat=Rs.getFloat(i+1);
                else if(CompleteColTypes[i]==java.sql.Date.class || CompleteColTypes[i]==java.util.Date.class)
                    dat=Rs.getDate(i+1);
                else
                    dat=Rs.getString(i+1);
                CompleteLine.add(dat);
                
                //Antes de adicionar aos dados visiveis é preciso verificar se a coluna é visivel
                if(j<VisibleTitle.size() && CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(j))){
                    VisibleLine.add(dat);
                    j++;
                }
            }
            
            //Verificar se item já não existe
            for(i=0; i<CompleteData.size(); i++){
                if(CompleteData.get(i).equals(CompleteLine)){
                    RowIndex=i;
                    break;
                }
            }
            
            //Dado não existe na tabela, adicionar
            if(RowIndex<0){
                CompleteData.add(CompleteLine);
                VisibleData.add(VisibleLine);
                RowEditable.add(Boolean.TRUE);
            } else { //Dado existe, selecionar
                this.setRowSelectionInterval(RowIndex,RowIndex);
            }
        }
        tmdModelo.fireTableDataChanged();
    }
    
    public Vector<Object> removeRow(int row){
        VisibleData.remove(row);
        Vector<Object> Vet=CompleteData.remove(row);
        RowEditable.remove(row);
        tmdModelo.fireTableDataChanged();
        return Vet;
    }
    
    public void moveRow(int OldPos, int NewPos){
        //NewPos--;
        
        if(NewPos<0) NewPos=0;
        else if(NewPos==this.CompleteData.size()) NewPos=this.CompleteData.size()-1;
        
        Vector<Object> VisRow = VisibleData.remove(OldPos);
        VisibleData.insertElementAt(VisRow,NewPos);
        
        Vector<Object> CompRow = CompleteData.remove(OldPos);
        CompleteData.insertElementAt(CompRow,NewPos);
        
        Boolean Edit=RowEditable.remove(OldPos);
        RowEditable.insertElementAt(Edit,NewPos);
        
        tmdModelo.fireTableDataChanged();
        
        this.setSelectedRow(NewPos);
    }
    
    public Vector<Object> resultSetToTableVectorRow(java.sql.ResultSet Rs) throws java.sql.SQLException{
        int ColCount=Rs.getMetaData().getColumnCount();
        Vector<Object> CompleteLine=new Vector<Object>();
        if(Rs.next()){
            for(int i=0; i<ColCount; i++){
                Object dat=null;
                if(CompleteColTypes[i]==java.lang.Boolean.class)
                    dat=Rs.getBoolean(i+1);
                else if(CompleteColTypes[i]==java.lang.Long.class || CompleteColTypes[i]==java.lang.Integer.class)
                    dat=Rs.getLong(i+1);
                else if(CompleteColTypes[i]==java.lang.Double.class)
                    dat=Rs.getDouble(i+1);
                else if(CompleteColTypes[i]==java.lang.Float.class)
                    dat=Rs.getFloat(i+1);
                else if(CompleteColTypes[i]==java.sql.Date.class || CompleteColTypes[i]==java.util.Date.class)
                    dat=Rs.getDate(i+1);
                else
                    dat=Rs.getString(i+1);
                CompleteLine.add(Rs.getString(i+1));
            }
        }
        return CompleteLine;
    }
    
    public Vector<Object> getNewEmptyRow(){
        Vector<Object> Ret=new Vector<Object>();
        for(int i=0; i<CompleteTitle.size(); i++){
            Ret.add(new String(""));
        }
        return Ret;
    }
    
    public int indexOfRow(Vector<Object> RowData) throws java.sql.SQLException{
        int i, RowIndex=-1;
        for(i=0; i<CompleteData.size(); i++){
            if(CompleteData.get(i).equals(RowData)){
                RowIndex=i;
                break;
            }
        }
        return RowIndex;
    }
    
    public void setVisible(String ColName, boolean Visibility){
        if(Visibility)
            showColumn(ColName);
        else
            hideColumn(ColName);
    }
    
    
    public void hideColumn(String ColName){
        //Encontra o indice da coluna a ser escondida
        int ColIndex=getVisibleColumnIndex(ColName);
        
        //Retira a coluna dos dados visiveis
        hideColumn(ColIndex);
    }
    
    public void hideColumn(int VisibleColIndex){
        if(VisibleColIndex>=0 && VisibleColIndex<VisibleTitle.size()){
            VisibleTitle.remove(VisibleColIndex);
            for(int i=0; i<VisibleData.size(); i++)
                VisibleData.get(i).remove(VisibleColIndex);
            
            tmdModelo.setDataVector(VisibleData,VisibleTitle);
            setColumnSize();
        }
    }
    
    public void showColumn(String ColName){
        //Encontra a coluna a ser exibida
        int ColIndex=getCompleteColumnIndex(ColName);
        
        //Se encontrou a coluna diacordo com o indice dos dados completos
        showColumn(ColIndex);
    }
    
    public void showColumn(int VisibleColIndex){
        int i, j;
        
        if(VisibleColIndex>=0){
            //Verificar se o usário não está tentando exibir uma coluna que já está visivel.
            if(getVisibleColumnIndex(VisibleColIndex)>=0) return;
            
            //Encontrar o indice relativo ao dados visiveis
            for(i=0, j=0; i<VisibleColIndex; i++){
                if(j<VisibleTitle.size() && CompleteTitle.get(i).equalsIgnoreCase(VisibleTitle.get(j)))
                    j++;
            }
            
            //Adiciona a coluna escondida de volta ao dados visiveis
            VisibleTitle.insertElementAt(CompleteTitle.get(VisibleColIndex),j);
            for(i=0; i<VisibleData.size(); i++){
                VisibleData.get(i).insertElementAt(CompleteData.get(i).get(VisibleColIndex),j);
            }
            
            //Refaz o modelo de exibição da tabela com os novos dados
            tmdModelo.setDataVector(VisibleData,VisibleTitle);
            setColumnSize();
        }
    }
    
    public void setSelectedRow(int Row){
        this.setRowSelectionInterval(Row,Row);
    }
    
    public void XprisTableMouseClicked(){
        int Row=this.getSelectedRow();
        int Col=this.getSelectedColumn();
        
        if(this.getSelectedRowCount()==1){
            
            if(this.getRowSelectionAllowed() && this.getColumnSelectionAllowed()){
                //Seleção de celula, comparar linha e coluna
                if(Row==LastSelectedRow && Col==LastSelectedCol){
                    LastSelectedRow=-1;
                    LastSelectedCol=-1;
                    this.clearSelection();
                } else{
                    LastSelectedRow=Row;
                    LastSelectedCol=Col;
                }
            } else{
                //Selecao de Linha
                if(Row==LastSelectedRow){
                    LastSelectedRow=-1;
                    this.clearSelection();
                } else{
                    LastSelectedRow=Row;
                }
            }
        }
    }
    
    public void setAlternateBackgroundColor(boolean f){
        this.AlternateBackgroundColor=f;
        this.repaint();
    }
    
    public void sort(String ColName, Class Type){
        java.util.Collections.sort(CompleteData,new XprisTableComparator(getColumnIndex(ColName),Type));
        java.util.Collections.sort(VisibleData,new XprisTableComparator(getVisibleColumnIndex(ColName),Type));
        
        //tmdModelo.fireTableDataChanged();
        tmdModelo.setDataVector(VisibleData,VisibleTitle);
        setColumnSize();
    }
    
    public void setHeaderRenderer(){
        this.getTableHeader().setDefaultRenderer(new XprisHeaderRenderer());
    }
    
    public class XprisTableComparator implements java.util.Comparator {
        
        protected Class Tipo=java.lang.String.class;
        public int ColIndex;
        
        public XprisTableComparator(int ColIndex, Class Tipo){
            this.Tipo=Tipo;
            this.ColIndex=ColIndex;
        }
        
        public int compare(Object o1, Object o2){
            java.util.Vector<Object> V1=(java.util.Vector<Object>)o1;
            java.util.Vector<Object> V2=(java.util.Vector<Object>)o2;
            
            double Diff;
            
            if(Tipo==java.lang.Integer.class){
                Diff=Integer.parseInt(V1.get(ColIndex).toString())-Integer.parseInt(V2.get(ColIndex).toString());
            } else if(Tipo==java.lang.Double.class){
                Diff=Double.parseDouble(V1.get(ColIndex).toString())-Double.parseDouble(V2.get(ColIndex).toString());
                if(Diff>0) Diff+=1;
                else if(Diff<0) Diff-=1;
            } else if(Tipo==java.sql.Time.class){
                String T1=V1.get(ColIndex).toString().replace(':','0');
                String T2=V2.get(ColIndex).toString().replace(':','0');
                Diff=Integer.parseInt(T1)-Integer.parseInt(T2);
            } else if(Tipo==java.sql.Date.class || Tipo==java.util.Date.class){
                java.util.Date Dt1=(java.util.Date)V1.get(ColIndex);
                java.util.Date Dt2=(java.util.Date)V2.get(ColIndex);
                Diff=Dt1.compareTo(Dt2);
            } else{ //String
                Diff=V1.get(ColIndex).toString().compareTo(V2.get(ColIndex).toString());
            }
            return (int)Diff;
        }
    }
    
    protected class XprisTableModel extends DefaultTableModel{
        
        protected XprisTableModel(){
            super();
        }
        
        public boolean isCellEditable(int row, int col){
            return CompleteColEditable[getCompleteColumnIndex(col)]&RowEditable.get(row).booleanValue();
        }
        
        public Class getColumnClass(int columnIndex) {
            return CompleteColTypes[getCompleteColumnIndex(columnIndex)];
        }
    };
    
    class XprisHeaderRenderer extends javax.swing.table.DefaultTableCellRenderer {
        
        public XprisHeaderRenderer() {
        }
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            XprisTable xtb=(XprisTable)table;
            int ColIndex=xtb.getCompleteColumnIndex(column);
            
            String[] Par=value.toString().split("#");
            String[] S=Par[0].split("\n");
            javax.swing.JPanel P1=new javax.swing.JPanel(new java.awt.GridLayout(S.length,1));
            P1.setBorder(javax.swing.BorderFactory.createMatteBorder(0,0,1,1,java.awt.Color.GRAY));
            P1.setPreferredSize(new java.awt.Dimension(xtb.CompleteColSizes[column],26*S.length));
            
            int Cor=-1;
            if(Par.length>1){
                //System.out.println(Par[1]);
                Cor=Integer.parseInt(Par[1],16);
            }
            
            for(int i=0; i<S.length; i++){
                javax.swing.JLabel L1=new javax.swing.JLabel(S[i],javax.swing.JLabel.CENTER);
                if(Cor>=0){
                    L1.setForeground(new java.awt.Color(Cor));
                }
                P1.add(L1);
            }
            javax.swing.LookAndFeel.installColorsAndFont(P1,"TableHeader.background","TableHeader.foreground","TableHeader.font");
            javax.swing.LookAndFeel.installBorder(P1,"TableHeader.cellBorder");
            P1.updateUI();
            return P1;
        }
    };
    
    class XprisTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        
        protected static final int MASCARA_COR = 0xFFFFFF;
        protected static final int MASCARA_ALINHAMENTO = 0xFF000000;
        
        public XprisTableCellRenderer() {
            super();
        }
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component Comp=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            try{
                String V=(String)value;
                
                javax.swing.JLabel Lab=(javax.swing.JLabel)Comp;
                if(!isSelected)
                    Lab.setForeground(java.awt.Color.BLACK);
                else
                    Lab.setForeground(java.awt.Color.WHITE);
                Lab.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                
                if(AlternateBackgroundColor && !isSelected){
                    Lab.setOpaque(true);
                    if(row%2==1){
                        Lab.setBackground(new java.awt.Color(235,235,235));
                    }else {
                        Lab.setBackground(java.awt.Color.WHITE);
                    }
                }
                
                String Toks[]=Lab.getText().split("#");
                if(Toks.length>1){
                    Lab.setText(Toks[0]);
                    int Parametro=Integer.parseInt(Toks[1]);
                    
                    int ParCor=Parametro&MASCARA_COR;
                    if(ParCor!=0){
                        if(!isSelected)
                            Lab.setForeground(new java.awt.Color(ParCor));
                    }
                    
                    int ParAlinhamento=Parametro&MASCARA_ALINHAMENTO;
                    if(ParAlinhamento!=0){
                        if(ParAlinhamento==CELULA_ALINHADO_ESQUERDA)
                            Lab.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                        else if(ParAlinhamento==CELULA_ALINHADO_DIREITA)
                            Lab.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
                        else if(ParAlinhamento==CELULA_ALINHADO_CENTRO)
                            Lab.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    }
                }
                return Lab;
                
            }catch(Exception e){
                
            }
            return Comp;
        }
    };
    
    public class XprisDoubleCellEditor extends javax.swing.DefaultCellEditor {
        javax.swing.JFormattedTextField ftf;
        java.text.NumberFormat doubleFormat;
        private boolean DEBUG = false;
        
        public XprisDoubleCellEditor() {
            super(new javax.swing.JFormattedTextField());
            ftf = (javax.swing.JFormattedTextField)getComponent();
            
            //Set up the editor for the integer cells.
            doubleFormat = java.text.DecimalFormat.getInstance();
            javax.swing.text.NumberFormatter decFormatter = new javax.swing.text.NumberFormatter(doubleFormat);
            decFormatter.setFormat(doubleFormat);
            
            ftf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(decFormatter));
            ftf.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            ftf.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
            
            //React when the user presses Enter while the editor is
            //active.  (Tab is handled as specified by
            //JFormattedTextField's focusLostBehavior property.)
            ftf.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "check");
            ftf.getActionMap().put("check", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!ftf.isEditValid()) { //The text is invalid.
                        if (userSaysRevert()) { //reverted
                            ftf.postActionEvent(); //inform the editor
                        }
                    } else try {              //The text is valid,
                        ftf.commitEdit();     //so use it.
                        ftf.postActionEvent(); //stop editing
                    } catch (java.text.ParseException exc) { }
                }
            });
        }
        
        //Override to invoke setValue on the formatted text field.
        public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
            javax.swing.JFormattedTextField ftf=(javax.swing.JFormattedTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
            ftf.setValue(value);
            ftf.selectAll();
            return ftf;
        }
        
        //Override to ensure that the value remains an Integer.
        public Object getCellEditorValue() {
            javax.swing.JFormattedTextField ftf=(javax.swing.JFormattedTextField)getComponent();
            Object o = ftf.getValue();
            if (o instanceof Double || o instanceof Float || o instanceof Integer) {
                return o;
            } else if (o instanceof Number) {
                return new Double(((Number)o).doubleValue());
            } else {
                if (DEBUG) {
                    System.out.println("getCellEditorValue: o isn't a Number");
                }
                try {
                    return doubleFormat.parseObject(o.toString());
                } catch (java.text.ParseException exc) {
                    System.err.println("getCellEditorValue: can't parse o: " + o);
                    return null;
                }
            }
        }
        
        //Override to check whether the edit is valid,
        //setting the value if it is and complaining if
        //it isn't.  If it's OK for the editor to go
        //away, we need to invoke the superclass's version
        //of this method so that everything gets cleaned up.
        public boolean stopCellEditing() {
            javax.swing.JFormattedTextField ftf=(javax.swing.JFormattedTextField)getComponent();
            if (ftf.isEditValid()) {
                try {
                    ftf.commitEdit();
                } catch (java.text.ParseException exc) { }
                
            } else { //text is invalid
                if (!userSaysRevert()) { //user wants to edit
                    return false; //don't let the editor go away
                }
            }
            return super.stopCellEditing();
        }
        
        /**
         * Lets the user know that the text they entered is
         * bad. Returns true if the user elects to revert to
         * the last good value.  Otherwise, returns false,
         * indicating that the user wants to continue editing.
         */
        protected boolean userSaysRevert() {
            java.awt.Toolkit.getDefaultToolkit().beep();
            ftf.selectAll();
            Object[] options = {" Editar ",
            "Retornar"};
            int answer = javax.swing.JOptionPane.showOptionDialog(
                    javax.swing.SwingUtilities.getWindowAncestor(ftf),
                    "O valor deve ser um número inteiro ou decimal separado por virgula.\n"
                    + "Você pode continuar editando ou retornar ao ultimo valor válido. ",
                    "Texto Inválido Digitado",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE,
                    null,options, options[1]);
            
            if (answer == 1) { //Revert!
                ftf.setValue(ftf.getValue());
                return true;
            }
            return false;
        }
    }
    
};
