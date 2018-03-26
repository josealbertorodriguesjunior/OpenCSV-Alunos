/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import helper.ConnectionFactory;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author junior
 */
public class Main extends ConnectionFactory {

    private static final String SAMPLE_CSV_FILE_PATH = "./alunos.csv";

    public static void main(String[] args) throws IOException, SQLException {
        try (
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();) {

            List<String[]> records = csvReader.readAll();

            Connection conexao = createConnection();

            for (int i = 1; i < records.size(); i++) {

                String[] get = records.get(i);

                System.out.println(i + " - " + get[6]);

                String check = "SELECT tbusuarios_login FROM tbusuarios WHERE tbusuarios_login = '" + get[7] + get[8] + "'";
                PreparedStatement pstm = conexao.prepareStatement(check);
                ResultSet rs = pstm.executeQuery();
                if (!rs.next()) {
                    //Insere na tbpessoas
                    String sql = "INSERT INTO tbpessoas(tbpessoas_nome,tbpessoas_sexo,tbpessoas_ufEnd,tbpessoas_cidade,tbpessoas_endereco,tbpessoas_endereconumero,tbpessoas_bairro,tbpessoas_cep,"
                            + "tbpessoas_dataCadastro,tbpessoas_ativo,tbpessoas_dataNasc,tbpessoas_cidadeNascimento,tbpessoas_rg,"
                            + "tbpessoas_mae,tbpessoas_pai,tbpessoas_tipo_logradouro,tbpessoas_ufNasc)"
                            + " VALUES(?,?,(select tbufs_id from tbufs where tbufs_descricao = '" + get[55] + "'),?,?,?,?,?,now(),"
                            + "'S',?,?,?,?,?,?,(select tbufs_id from tbufs where tbufs_descricao = '" + get[28] + "'))";

                    PreparedStatement psCadastraPessoas = conexao.prepareStatement(sql);

                    psCadastraPessoas.setString(1, get[6]);
                    psCadastraPessoas.setString(2, get[14]);
                    psCadastraPessoas.setString(3, get[54]);
                    psCadastraPessoas.setString(4, get[49]);
                    psCadastraPessoas.setString(5, get[50]);
                    psCadastraPessoas.setString(6, get[52]);
                    psCadastraPessoas.setString(7, get[53]);
                    psCadastraPessoas.setString(8, get[26]);
                    psCadastraPessoas.setString(9, get[27]);
                    psCadastraPessoas.setString(10, get[11] + get[12]);
                    psCadastraPessoas.setString(11, get[42]);
                    psCadastraPessoas.setString(12, get[43]);
                    psCadastraPessoas.setInt(13, 0);
                    System.out.println(psCadastraPessoas);
                    psCadastraPessoas.executeUpdate();

                    //Insere na tbusuarios
                    String sqlInsertUser = "INSERT INTO tbusuarios (tbusuarios_login,tbusuarios_senha,tbusuarios_ativo,tbusuarios_usa_rede_social,tbperfil_id,tbusuarios_tipo) VALUES(?,'1234mudar','S','S',14,'A')";
                            
                    PreparedStatement psCadastroUsuarios = conexao.prepareStatement(sqlInsertUser);

                    psCadastroUsuarios.setString(1, get[7] + get[8]);
                    psCadastroUsuarios.executeUpdate();

                    get[3] = get[3].replace(" EMEI", "%");

                    String sqlInsereAluno = "INSERT INTO tbcadastro_alunos (tbcadastro_alunos_ra, tbpessoas_id,tbcadastro_alunos_datacadastro,tbusuarios_id,tbescolas_id_pretendida,tbcadastro_alunos_transporteEscolar) VALUES(?,(select max(tbpessoas_id) from tbpessoas),now(),(select max(tbusuarios_id)from tbusuarios),(select tbescolas_id from tbescolas where tbescolas_descricao like '%" + get[3] + "'),'S')";

                    PreparedStatement psCadastroAlunos = conexao.prepareStatement(sqlInsereAluno);

                    psCadastroAlunos.setString(1, get[7] + get[8]);

                    System.out.println(psCadastroAlunos);

                    psCadastroAlunos.executeUpdate();

                    //Insere na tbinscricao_alunos
                    String sqlInsertInscricao = "INSERT INTO tbinscricao_alunos(tbinscricao_alunos_irmao,tbinscricao_alunos_datacadastro,tbcadastro_alunos_id,tbescolas_id,tbanos_series_id,tbperiodos_id)"
                            + "VALUES ('',now(),(select tbcadastro_alunos_id from tbcadastro_alunos where tbcadastro_alunos_ra like '%" + get[7] + get[8] + "%'),(select tbescolas_id from tbescolas where tbescolas_descricao like '%" + get[3] + "'),(select tbanos_series_id from tbturmas where tbturmas_codigo like '%" + get[58] + "%'),(select tbperiodos_id from tbturmas where tbturmas_codigo like '%" + get[58] + "%'))";

                    PreparedStatement psInscricaoAluno = conexao.prepareStatement(sqlInsertInscricao);
                    System.out.println(psInscricaoAluno);
                    psInscricaoAluno.executeUpdate();

                    //Insere na tabela tbalunosXescolas
                    String sqlInsertAlunoXEscolas = "INSERT INTO tbinscricao_alunos_x_tbescolas(tbinscricao_alunos_x_tbescolas_id, tbinscricao_alunos_id, tbescolas_id, "
                            + "tbanos_series_id, tbinscricao_alunos_x_tbescolas_situacao) "
                            + "VALUES (null, (select tbinscricao_alunos_id from tbinscricao_alunos where tbcadastro_alunos_id=(select c.tbcadastro_alunos_id from tbcadastro_alunos c "
                            + "where c.tbcadastro_alunos_ra = '" + get[7] + get[8] + "')),"
                            + "(select tbescolas_id from tbescolas where tbescolas_descricao like '%" + get[3] + "'),"
                            + "(select tbanos_series_id from tbturmas where tbturmas_codigo like '%" + get[58] + "%'),'O');";
                    PreparedStatement psAlunoXEscola = conexao.prepareStatement(sqlInsertAlunoXEscolas);
                    System.out.println(psAlunoXEscola);
                    psAlunoXEscola.executeUpdate();

                    //Insere na tabela tbsituacao_alunos
                    String sqlInsertSituacaoAlunos = "INSERT INTO tbsituacao_alunos(tbsituacao_alunos_status, tbsituacao_alunos_data, tbcadastro_alunos_id, tbusuarios_id)"
                            + "VALUES ((select tbcadastro_alunos_situacao from tbcadastro_alunos where tbcadastro_alunos_ra='" + get[7] + get[8] + "'),now(),(select tbcadastro_alunos_id "
                            + "from tbcadastro_alunos where tbcadastro_alunos_ra= '" + get[7] + get[8] + "'),(select tbusuarios_id from tbusuarios where tbusuarios_login = '" + get[7] + get[8] + "'));	";

                    PreparedStatement psSituacaoAlunos = conexao.prepareStatement(sqlInsertSituacaoAlunos);
                    System.out.println(psSituacaoAlunos);
                    psSituacaoAlunos.executeUpdate();

                    //Insere na tbalunos_localizacao
                    String sqlInsertAlunosLocalizacao = "INSERT INTO tbalunos_localizacao (tbcadastro_alunos_id, tbturmas_id, tbalunos_localizacao_data, tbalunos_localizacao_motivo)"
                            + "VALUES ((select tbcadastro_alunos_id from tbcadastro_alunos where tbcadastro_alunos_ra= '" + get[7] + get[8] + "'),(select tbturmas_id from tbturmas where tbturmas_codigo like '%" + get[58] + "%'),now(), 'M');";

                    PreparedStatement psAlunosLocalizacao = conexao.prepareStatement(sqlInsertAlunosLocalizacao);
                    System.out.println(psAlunosLocalizacao);
                    psAlunosLocalizacao.executeUpdate();

                    //Insere na tbmatriculas_alunos
                    String sqlInsertMatricula = "INSERT INTO tbmatriculas_alunos(tbinscricao_alunos_x_tbescolas_id, tbturmas_id, tbanos_letivo_id)"
                            + "VALUES ((select tbinscricao_alunos_x_tbescolas_id from tbinscricao_alunos_x_tbescolas where tbinscricao_alunos_id = "
                            + "(select i.tbinscricao_alunos_id from tbinscricao_alunos i where i.tbcadastro_alunos_id=(select c.tbcadastro_alunos_id from tbcadastro_alunos c where c.tbcadastro_alunos_ra= '" + get[7] + get[8] + "'))),"
                            + "(select tbturmas_id from tbturmas where tbturmas_codigo like '%" + get[58] + "%'),11);";

                    PreparedStatement psMatricula = conexao.prepareStatement(sqlInsertMatricula);
                    System.out.println(psMatricula);
                    psMatricula.executeUpdate();
                }
            }
            conexao.close();
        }
    }

}
