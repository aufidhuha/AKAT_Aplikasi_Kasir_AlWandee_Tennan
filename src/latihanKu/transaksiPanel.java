/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package latihanKu;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import latihanKu.cetakStrukFrame;
/**
 *
 * @author ASUS
 */
public class transaksiPanel extends javax.swing.JPanel {

    /**
     * Creates new form transaksiPanel
     */
    public transaksiPanel() {
        initComponents();
        
        tanggal();
        model_tabel();
        auto_NoFa();
        auto_Customer();
        nonEditable();
    }

    void tanggal(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
            txtTanggal.setText(dateFormat.format(new Date()));
            }
        });
        timer.start();
        txtTanggal.setEditable(false);
    }
           
    void model_tabel(){
        
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("ID Produk");
        model.addColumn("Nama Produk");
        model.addColumn("Harga Produk");
        model.addColumn("Jumlah");
        model.addColumn("Total Harga");
        
        tabelTransaksi.setModel(model);
    }
    
    private void auto_NoFa(){
        
        try {
            Connection cnVar = koneksi.getKoneksi();
            Statement stVar = cnVar.createStatement();
            String sql = "SELECT * FROM transaksi ORDER BY NoFaktur DESC";
            ResultSet rsVar = stVar.executeQuery(sql);
            
            if (rsVar.next()) {
                String NoFaktur = rsVar.getString("NoFaktur").substring(3);
                String transaksi = "" + (Integer.parseInt(NoFaktur) + 1);
                String nol = "";
                
                if (transaksi.length() == 1) {
                    nol = "000";
                } else if (transaksi.length() == 2) {
                    nol = "00";
                } else if (transaksi.length() == 3) {
                    nol = "0";
                } else if (transaksi.length() == 4) {
                    nol = "";
                }
                txtNoTransaksi.setText("TRA" + nol + transaksi);
            } else {
                txtNoTransaksi.setText("TRA0001");
            }
            
            rsVar.close();
            stVar.close();
        } catch (SQLException sQLException) {
            JOptionPane.showMessageDialog(null, "Auto Number Transaksi Error " + sQLException.getMessage());
        }
        txtNoTransaksi.setEditable(false);
    }
    
    private void auto_Customer(){
        
        try {
            Connection cnVar = koneksi.getKoneksi();
            Statement stVar = cnVar.createStatement();
            String sql = "SELECT * FROM transaksi ORDER BY ID_Customers DESC";
            ResultSet rsVar = stVar.executeQuery(sql);
            
            if (rsVar.next()) {
                String idCustomer = rsVar.getString("ID_Customers").substring(3);
                String customer = "" + (Integer.parseInt(idCustomer) + 1);
                String nol = "";
                
                if (customer.length() == 1) {
                    nol = "000";
                } else if (customer.length() == 2) {
                    nol = "00";
                } else if (customer.length() == 3) {
                    nol = "0";
                } else if (customer.length() == 4) {
                    nol = "";
                }
                txtIDCustomers.setText("CST" + nol + customer);
            } else {
                txtIDCustomers.setText("CST0001");
            }
            
            rsVar.close();
            stVar.close();
        } catch (SQLException sQLException) {
            JOptionPane.showMessageDialog(null, "Auto Number Customers Error " + sQLException.getMessage());
        }
        txtIDCustomers.setEditable(false);
    }
    
    void totalBiaya(){
        int jumlahBaris = tabelTransaksi.getRowCount();
        int jumlahTotal = 0;
        
        int harga, jumlahBeli;
        
        for (int i = 0; i < jumlahBaris; i++) {
           harga = Integer.parseInt(tabelTransaksi.getValueAt(i, 2).toString());
           jumlahBeli = Integer.parseInt(tabelTransaksi.getValueAt(i, 3).toString());
           jumlahTotal = jumlahTotal + (harga * jumlahBeli);
        }
        txtTotalBayar.setText("Rp. " + jumlahTotal);
    }
    
    void nonEditable(){
        txtIDProduk.setEditable(false);
        txtNamaProduk.setEditable(false);
        txtHargaProduk.setEditable(false);
        txtTotalBayar.setEditable(false);
        txtKembalian.setEditable(false);
        txtTotalBayar.setText("Rp. 0");
        txtKembalian.setText("Rp. 0");
    }

    void clearProduk(){
        txtIDProduk.setText("");
        txtNamaProduk.setText("");
        txtHargaProduk.setText("");
        txtJumlah.setText("");
    }    
    
    void reset(){
       txtIDProduk.setText(null);
       txtNamaProduk.setText(null);
       txtHargaProduk.setText(null);
       txtJumlah.setText(null);
       txtTotalBayar.setText("Rp. 0");
       txtBayar.setText("");
       txtKembalian.setText("Rp. 0");
       emptyTable();
    }
    
    void emptyTable(){
         DefaultTableModel model = (DefaultTableModel) tabelTransaksi.getModel();
        while (model.getRowCount() > 0) {            
            model.removeRow(0);
        }
        totalBiaya();
    }
    
    void tambahTransaksi(){
        
      try {
          
        int jumlah, harga, total;
        jumlah = Integer.parseInt(txtJumlah.getText());
        
        if (txtIDProduk.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harap memilih produk");
            txtJumlah.setText("");
            return;
        } else if (jumlah <= 0) {
            JOptionPane.showMessageDialog(null, "Harap mengisi jumlah lebih dari 0");
            txtJumlah.setText("");
            return;
        } else {
            
            harga = Integer.parseInt(txtHargaProduk.getText());                
            total = harga * jumlah;
                
            txtTotalBayar.setText(String.valueOf(total));
            load_data();
            totalBiaya();
            clearProduk();
        
           }  
        
       } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(null, "Input-an Tidak Valid");
            txtJumlah.setText("");
       }
    }
    
    void load_data(){
        DefaultTableModel model = (DefaultTableModel) tabelTransaksi.getModel();
      
        String idProduk = txtIDProduk.getText();
        String namaProduk = txtNamaProduk.getText();
        String hargaProduk = txtHargaProduk.getText();
        String jumlah = txtJumlah.getText();
        String total = txtTotalBayar.getText();
        model.addRow(
               new Object[] {idProduk, namaProduk, hargaProduk, jumlah, total}
        );
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNoTransaksi = new javax.swing.JTextField();
        txtIDCustomers = new javax.swing.JTextField();
        txtNamaCustomer = new javax.swing.JTextField();
        buttonMenuMakanan = new javax.swing.JButton();
        buttonMenuMinuman = new javax.swing.JButton();
        buttonMenuPS = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelTransaksi = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtIDProduk = new javax.swing.JTextField();
        txtNamaProduk = new javax.swing.JTextField();
        txtHargaProduk = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTotalBayar = new javax.swing.JTextField();
        txtBayar = new javax.swing.JTextField();
        txtKembalian = new javax.swing.JTextField();
        buttonReset = new javax.swing.JButton();
        buttonBatall = new javax.swing.JButton();
        buttonSimpan = new javax.swing.JButton();
        buttonCetak = new javax.swing.JButton();
        buttonMuatUlang = new javax.swing.JButton();
        txtTanggal = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        buttonLaporanTransaksi = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 248, 231));

        jPanel2.setBackground(new java.awt.Color(189, 195, 199));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("TRANSAKSI");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/latihanKu/images/icons8-transaction-43 (1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(478, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(494, 494, 494))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(189, 195, 199));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("NO TRANSAKSI");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("ID CUSTOMER");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("NAMA CUSTOMER");

        txtNoTransaksi.setEditable(false);
        txtNoTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        txtNoTransaksi.setForeground(new java.awt.Color(0, 0, 0));
        txtNoTransaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        txtIDCustomers.setEditable(false);
        txtIDCustomers.setBackground(new java.awt.Color(255, 255, 255));
        txtIDCustomers.setForeground(new java.awt.Color(0, 0, 0));
        txtIDCustomers.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        txtNamaCustomer.setBackground(new java.awt.Color(255, 255, 255));
        txtNamaCustomer.setForeground(new java.awt.Color(0, 0, 0));
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        buttonMenuMakanan.setBackground(new java.awt.Color(255, 183, 77));
        buttonMenuMakanan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonMenuMakanan.setForeground(new java.awt.Color(255, 255, 255));
        buttonMenuMakanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/latihanKu/images/icons8-search-16.png"))); // NOI18N
        buttonMenuMakanan.setText("MENU MAKANAN");
        buttonMenuMakanan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 15));
        buttonMenuMakanan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonMenuMakanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMenuMakananActionPerformed(evt);
            }
        });

        buttonMenuMinuman.setBackground(new java.awt.Color(109, 76, 65));
        buttonMenuMinuman.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonMenuMinuman.setForeground(new java.awt.Color(255, 255, 255));
        buttonMenuMinuman.setIcon(new javax.swing.ImageIcon(getClass().getResource("/latihanKu/images/icons8-search-16.png"))); // NOI18N
        buttonMenuMinuman.setText("MENU MINUMAN");
        buttonMenuMinuman.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 15));
        buttonMenuMinuman.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonMenuMinuman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMenuMinumanActionPerformed(evt);
            }
        });

        buttonMenuPS.setBackground(new java.awt.Color(126, 87, 194));
        buttonMenuPS.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonMenuPS.setForeground(new java.awt.Color(255, 255, 255));
        buttonMenuPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/latihanKu/images/icons8-search-16.png"))); // NOI18N
        buttonMenuPS.setText("MENU PLAY STATION");
        buttonMenuPS.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 15));
        buttonMenuPS.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonMenuPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMenuPSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(63, 63, 63)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNoTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(txtIDCustomers)
                    .addComponent(txtNamaCustomer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 298, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonMenuMinuman, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMenuMakanan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMenuPS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNoTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMenuMakanan, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIDCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMenuMinuman, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNamaCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMenuPS, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        tabelTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        tabelTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tabelTransaksi.setForeground(new java.awt.Color(0, 0, 0));
        tabelTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelTransaksi);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("ID PRODUK");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("NAMA PRODUK");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("HARGA PRODUK");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("JUMLAH");

        txtIDProduk.setBackground(new java.awt.Color(204, 204, 204));
        txtIDProduk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtIDProduk.setForeground(new java.awt.Color(0, 0, 0));
        txtIDProduk.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        txtNamaProduk.setBackground(new java.awt.Color(204, 204, 204));
        txtNamaProduk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtNamaProduk.setForeground(new java.awt.Color(0, 0, 0));
        txtNamaProduk.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        txtHargaProduk.setBackground(new java.awt.Color(204, 204, 204));
        txtHargaProduk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtHargaProduk.setForeground(new java.awt.Color(0, 0, 0));
        txtHargaProduk.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        txtJumlah.setBackground(new java.awt.Color(255, 255, 255));
        txtJumlah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtJumlah.setForeground(new java.awt.Color(0, 0, 0));
        txtJumlah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtJumlah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        txtJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJumlahActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("TOTAL BAYAR");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("BAYAR");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("KEMBALIAN");

        txtTotalBayar.setBackground(new java.awt.Color(204, 204, 204));
        txtTotalBayar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTotalBayar.setForeground(new java.awt.Color(0, 0, 0));
        txtTotalBayar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalBayar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 15));

        txtBayar.setBackground(new java.awt.Color(255, 255, 255));
        txtBayar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBayar.setForeground(new java.awt.Color(0, 0, 0));
        txtBayar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBayar.setToolTipText("");
        txtBayar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 15));
        txtBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBayarActionPerformed(evt);
            }
        });

        txtKembalian.setBackground(new java.awt.Color(204, 204, 204));
        txtKembalian.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtKembalian.setForeground(new java.awt.Color(0, 0, 0));
        txtKembalian.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtKembalian.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 15));

        buttonReset.setBackground(new java.awt.Color(97, 97, 97));
        buttonReset.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonReset.setForeground(new java.awt.Color(255, 255, 255));
        buttonReset.setText("RESET");
        buttonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetActionPerformed(evt);
            }
        });

        buttonBatall.setBackground(new java.awt.Color(244, 67, 54));
        buttonBatall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonBatall.setForeground(new java.awt.Color(255, 255, 255));
        buttonBatall.setText("BATAL");
        buttonBatall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBatallActionPerformed(evt);
            }
        });

        buttonSimpan.setBackground(new java.awt.Color(40, 167, 69));
        buttonSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonSimpan.setForeground(new java.awt.Color(255, 255, 255));
        buttonSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/latihanKu/images/icons8-note-20.png"))); // NOI18N
        buttonSimpan.setText("SIMPAN");
        buttonSimpan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonSimpan.setIconTextGap(6);
        buttonSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSimpanActionPerformed(evt);
            }
        });

        buttonCetak.setBackground(new java.awt.Color(0, 123, 255));
        buttonCetak.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonCetak.setForeground(new java.awt.Color(255, 255, 255));
        buttonCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/latihanKu/images/icons8-print-20.png"))); // NOI18N
        buttonCetak.setText("CETAK");
        buttonCetak.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonCetak.setIconTextGap(10);
        buttonCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCetakActionPerformed(evt);
            }
        });

        buttonMuatUlang.setBackground(new java.awt.Color(0, 123, 255));
        buttonMuatUlang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonMuatUlang.setForeground(new java.awt.Color(255, 255, 255));
        buttonMuatUlang.setText("MUAT ULANG");
        buttonMuatUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMuatUlangActionPerformed(evt);
            }
        });

        txtTanggal.setBackground(new java.awt.Color(255, 255, 255));
        txtTanggal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTanggal.setForeground(new java.awt.Color(0, 0, 0));
        txtTanggal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTanggal.setToolTipText("");
        txtTanggal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 15));
        txtTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTanggalActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("TANGGAL");

        buttonLaporanTransaksi.setBackground(new java.awt.Color(67, 160, 71));
        buttonLaporanTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonLaporanTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        buttonLaporanTransaksi.setText("LAPORAN TRANSAKSI");
        buttonLaporanTransaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonLaporanTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLaporanTransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtIDProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtNamaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(txtHargaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(buttonLaporanTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel12))
                                .addGap(53, 53, 53)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtTotalBayar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 865, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonReset, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(buttonBatall, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(buttonMuatUlang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buttonCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9))
                    .addComponent(jLabel5))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtIDProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtNamaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtHargaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonReset, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonBatall, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(buttonMuatUlang, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(buttonSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 41, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonCetak, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                .addGap(36, 36, 36))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(buttonLaporanTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonMenuMakananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMenuMakananActionPerformed
        // TODO add your handling code here:
        new dataMakanan().setVisible(true);
    }//GEN-LAST:event_buttonMenuMakananActionPerformed

    private void buttonMenuMinumanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMenuMinumanActionPerformed
        // TODO add your handling code here:
        new dataMinuman().setVisible(true);
    }//GEN-LAST:event_buttonMenuMinumanActionPerformed

    private void buttonMenuPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMenuPSActionPerformed
        // TODO add your handling code here:
        new dataPS().setVisible(true);
    }//GEN-LAST:event_buttonMenuPSActionPerformed

    private void txtJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJumlahActionPerformed
        // TODO add your handling code here:
        if (txtJumlah.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Harap mengisi jumlah");
            txtJumlah.setText("");  
            return;
        } else  {
            tambahTransaksi();
        }
    }//GEN-LAST:event_txtJumlahActionPerformed

    private void txtBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBayarActionPerformed
        // TODO add your handling code here:
        int total, bayar, kembalian; 
        try {
            String nilai = txtTotalBayar.getText().substring(4);
            total = Integer.parseInt(nilai.trim());
            bayar = Integer.parseInt(txtBayar.getText());
            
            if (total > bayar) {
                JOptionPane.showMessageDialog(null, "Uang tidak cukup");
            } else {
                kembalian = bayar - total;
                txtKembalian.setText("Rp. " + kembalian);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        }
    }//GEN-LAST:event_txtBayarActionPerformed

    private void buttonBatallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBatallActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabelTransaksi.getModel();
        int row = tabelTransaksi.getSelectedRow();
        if (row > -1) {
            model.removeRow(row);
            totalBiaya();
        } else {
            JOptionPane.showMessageDialog(null, "Harap memilih data yang ingin dibatalkan");
        }
    }//GEN-LAST:event_buttonBatallActionPerformed

    private void buttonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetActionPerformed
        // TODO add your handling code here:
        reset();
        
    }//GEN-LAST:event_buttonResetActionPerformed

    private void buttonSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSimpanActionPerformed
        // TODO add your handling code here:
        if (tabelTransaksi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Harap memilih produk untuk transaksi");
            return;
        }
        
        String noTransaksi =  txtNoTransaksi.getText();
        String idCustomer = txtIDCustomers.getText();
        String noFaktur = txtNoTransaksi.getText();
        String total = txtTotalBayar.getText().substring(4);
        int nilaiTotal =  Integer.parseInt(total);
        String namaCustomer = txtNamaCustomer.getText();
        String tanggal = txtTanggal.getText();
        
        Connection cnVar = koneksi.getKoneksi();
        
        try {
            String sqlNoFaktur = "SELECT * FROM transaksi WHERE NoFaktur = ?";
            PreparedStatement psVarNoFaktur = cnVar.prepareStatement(sqlNoFaktur);
            psVarNoFaktur.setString(1, noFaktur);
            ResultSet rsVarNoFaktur = psVarNoFaktur.executeQuery();
            
            
            String sqlIdCustomer = "SELECT * FROM customers WHERE ID_Customer = ?";
            PreparedStatement psVarIdCustomer = cnVar.prepareStatement(sqlIdCustomer);
            psVarIdCustomer.setString(1, idCustomer);
            ResultSet rsVarIdCustomer = psVarIdCustomer.executeQuery();
            
            if (rsVarNoFaktur.next() || rsVarIdCustomer.next()) {                
                JOptionPane.showMessageDialog(null, "Transaksi sudah dilakukan, silahkan Muat Ulang untuk Transaksi baru.");
                return;
                
            } else {
       
            String sqlCustomer = "INSERT INTO customers VALUES (?, ?)";
            PreparedStatement psVarCustomers = cnVar.prepareStatement(sqlCustomer);
            psVarCustomers.setString(1, idCustomer);
            psVarCustomers.setString(2, namaCustomer);
            psVarCustomers.executeUpdate();
            psVarCustomers.close();
            
            
            String sqlTransaksi = "INSERT INTO transaksi (NoFaktur, ID_Customers, TotalBeli, tanggal) VALUES (?, ?, ?, ?)";
            PreparedStatement psVarTransaski = cnVar.prepareStatement(sqlTransaksi);
            psVarTransaski.setString(1, noTransaksi);
            psVarTransaski.setString(2, idCustomer);
            psVarTransaski.setInt(3, nilaiTotal);
            psVarTransaski.setString(4, tanggal);
            psVarTransaski.executeUpdate();
            psVarTransaski.close();
            
            
            int baris = tabelTransaksi.getRowCount();
            String sqlDetailTransaksi = "INSERT INTO detail_transaksi (NoFa, ID_Produk, Nama_Produk, harga, jumlah, total) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psVarDetailTransaksi = cnVar.prepareStatement(sqlDetailTransaksi);
            for (int i = 0; i < baris; i++) {
                psVarDetailTransaksi.setString(1, noFaktur);
                psVarDetailTransaksi.setString(2, tabelTransaksi.getValueAt(i, 0).toString());
                psVarDetailTransaksi.setString(3, tabelTransaksi.getValueAt(i, 1).toString());
                psVarDetailTransaksi.setString(4, tabelTransaksi.getValueAt(i, 2).toString());
                psVarDetailTransaksi.setString(5, tabelTransaksi.getValueAt(i, 3).toString());
                psVarDetailTransaksi.setString(6, tabelTransaksi.getValueAt(i, 4).toString());
                psVarDetailTransaksi.executeUpdate();
            }
            psVarDetailTransaksi.close();            
            
            JOptionPane.showMessageDialog(null, "Transaksi Berhasil");            
            
            }
        } catch (SQLException sQLException) {
            JOptionPane.showMessageDialog(null, "Error : " + sQLException.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());            
        }
        
    }//GEN-LAST:event_buttonSimpanActionPerformed

    private void buttonMuatUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMuatUlangActionPerformed
        // TODO add your handling code here:
            auto_NoFa();
            auto_Customer();
            emptyTable();
            reset();
            txtNamaCustomer.setText(null);
    }//GEN-LAST:event_buttonMuatUlangActionPerformed

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void buttonCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCetakActionPerformed
       // TODO add your handling code here:
       new cetakStrukFrame().setVisible(true);
    }//GEN-LAST:event_buttonCetakActionPerformed

    private void buttonLaporanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLaporanTransaksiActionPerformed
        // TODO add your handling code here:
        new laporanFrame().setVisible(true);
    }//GEN-LAST:event_buttonLaporanTransaksiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBatall;
    private javax.swing.JButton buttonCetak;
    private javax.swing.JButton buttonLaporanTransaksi;
    private javax.swing.JButton buttonMenuMakanan;
    private javax.swing.JButton buttonMenuMinuman;
    private javax.swing.JButton buttonMenuPS;
    private javax.swing.JButton buttonMuatUlang;
    private javax.swing.JButton buttonReset;
    private javax.swing.JButton buttonSimpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelTransaksi;
    private javax.swing.JTextField txtBayar;
    public static javax.swing.JTextField txtHargaProduk;
    private javax.swing.JTextField txtIDCustomers;
    public static javax.swing.JTextField txtIDProduk;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtNamaCustomer;
    public static javax.swing.JTextField txtNamaProduk;
    private javax.swing.JTextField txtNoTransaksi;
    private javax.swing.JTextField txtTanggal;
    private javax.swing.JTextField txtTotalBayar;
    // End of variables declaration//GEN-END:variables
}
