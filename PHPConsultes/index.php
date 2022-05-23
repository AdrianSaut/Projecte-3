<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

  <title>Consulta paraules</title>
</head>
<?php

$tipus    = isset($_REQUEST['tipus'])   ? $_REQUEST['tipus']    : '';
$paraula  = isset($_REQUEST['paraula']) ? $_REQUEST['paraula']  : '';
$grup     = isset($_REQUEST['grup'])    ? $_REQUEST['grup']     : '';
$idP      = isset($_REQUEST['id'])      ? $_REQUEST['id']       : '';
$t        = isset($_REQUEST['t'])       ? $_REQUEST['t']        : '';
$usuariC  = isset($_REQUEST['usuariC']) ? $_REQUEST['usuariC']  : '';
$usuari   = isset($_REQUEST['usuari'])  ? $_REQUEST['usuari']   : '';
$paraula  = trim($paraula);
$paraula  = strtolower($paraula);
$grup     = trim($grup);
$grup     = strtolower($grup);
$con = new mysqli("192.168.1.218", "admin", "Fat/3232", "chat");
$msg = "";
if ($tipus == '1') {
  // echo "Consulta de paraules";


  if ($paraula != "" && $t == "1") {

    $sql = "SELECT * FROM DEL_paraules_prohibides WHERE paraula = '$paraula'";
    // echo $sql;
    $result = $con->query($sql);
    if ($result->num_rows > 0) {
      $row = $result->fetch_assoc();
      $id = $row['id'];
      $paraulabd = $row['paraula'];
      if ($paraula == $paraulabd) {
        $msg = "<p class='text-success'>La paraula <span class='font-weight-bold'>" . $paraula . "</span> amb id <span class='font-weight-bold'>" . $id . "</span> apareix a la base de dades</p>";
      }
    } else {
      $msg = "<p class='text-danger'> La paraula <span class='font-weight-bold'>" . $paraula . "</span> no apareix a la base de dades</p>";
    }
  } else if ($t == "2" && $grup != "") {
    $sql = "SELECT * FROM DEL_paraules_prohibides WHERE tipus = '$grup'";

    $result = $con->query($sql);
    if ($result->num_rows > 0) {
      $msg = "<table class='table table-striped table-bordered table-hover m-3'>" .
        "<thead class='thead-dark'>" .
        "<tr>" .
        "<th>Paraula</th>" .
        "<th>Tipus</th>" .
        "<th>Accions</th>" .
        "</tr>" .
        "</thead>" .
        "<tbody>";
      while ($row = $result->fetch_assoc()) {
        $id = $row['id'];
        $paraula = $row['paraula'];
        $tipus = $row['tipus'];
        $msg .= "<tr>" .
          "<td>" . $paraula . "</td>" .
          "<td>" . $tipus . "</td>" .
          "<td><a href='index.php?tipus=1&t=3&id=" . $id . "' class='btn btn-danger'>Eliminar</a></td>" .
          "</tr>";
      }
      $msg .= "</tbody>" .
        "</table>";
    } else {
      $msg = "No hi ha cap paraula amb aquest tipus";
    }
  } else if ($t == "3" && $idP != "") {
    $sql = "DELETE FROM DEL_paraules_prohibides WHERE id = '$idP'";
    $result = $con->query($sql);
    if ($result) {
      $msg = "<p class='text-success'>S'ha eliminat la paraula amb id <span class='font-weight-bold'>" . $idP . "</span> de la base de dades</p>";
    } else {
      $msg = "<p class='text-danger'>No s'ha pogut eliminar la paraula amb id <span class='font-weight-bold'>" . $idP . "</span> de la base de dades</p>";
    }
  }
?>

  <body>
    <nav class="m-3" aria-label="Page navigation example">
      <ul class="pagination">
        <li class="page-item"><a class="page-link" href="index.php">Torna</a></li>
      </ul>
    </nav>
    <div class="d-flex justify-content-center">
      <div class="d-flex justify-content-center">
        <div class="card m-3" style="width: 18rem;">
          <div class="card-body">
            <form>
              <div class="form-group">
                <label>Paraula</label>
                <input type="text" class="form-control" name="paraula" placeholder="Escriu paraula">
                <input type="hidden" name="tipus" value="1">
                <input type="hidden" name="t" value="1">
              </div>
              <button type="submit" class="btn btn-primary">Cerca</button>
            </form>
            <?php
            if ($t == "1") {
              echo $msg;
            }
            ?>
          </div>
        </div>
      </div>
      <div class="d-flex justify-content-center">
        <div class="card m-3" style="width: 18rem;">
          <div class="card-body">
            <form>
              <div class="form-group">
                <label>Grup de paraules (Tipus)</label>
                <input type="text" class="form-control" name="grup" placeholder="Escriu grup">
                <input type="hidden" name="tipus" value="1">
                <input type="hidden" name="t" value="2">
              </div>
              <button type="submit" class="btn btn-primary">Cerca</button>
            </form>
            <?php
            if ($t == "3") {
              echo $msg;
            }
            ?>
          </div>
        </div>
      </div>
      <?php
      if ($t == "2") {
        echo $msg;
      }
      ?>
    </div>
  </body>

<?php
} elseif ($tipus == '2') {
  if ($paraula != "" && $t == "1" && $grup != "") {
    $sql = "SELECT * FROM DEL_paraules_prohibides WHERE paraula = '$paraula' AND tipus = '$grup'";
    $result = $con->query($sql);
    if ($result->num_rows > 0) {
      $msg = "<p class='text-danger'> La paraula <span class='font-weight-bold'>" . $paraula . "</span> ja existeix a la base de dades</p>";
    } else {
      $sql = "INSERT INTO DEL_paraules_prohibides (paraula, tipus) VALUES ('$paraula', '$grup')";
      $result = $con->query($sql);
      if ($result) {
        $msg = "<p class='text-success'>La paraula <span class='font-weight-bold'>" . $paraula . "</span> de tipus <span class='font-weight-bold'>" . $grup . "</span> s'ha afegit correctament a la base de dades</p>";
      } else {
        $msg = "<p class='text-danger'>La paraula <span class='font-weight-bold'>" . $paraula . "</span> de tipus <span class='font-weight-bold'>" . $grup . "</span> no s'ha pogut afegir a la base de dades</p>";
      }
    }
  }

?>

  <body>
    <nav class="m-3" aria-label="Page navigation example">
      <ul class="pagination">
        <li class="page-item"><a class="page-link" href="index.php">Torna</a></li>
      </ul>
    </nav>
    <div class="d-flex justify-content-center">
      <div class="card m-3" style="width: 18rem;">
        <div class="card-body">
          <form>
            <div class="form-group">
              <label>Inserir Paraules</label>
              <input type="text" class="form-control" name="paraula" placeholder="Escriu paraula">
              <br>
              <input type="text" class="form-control" name="grup" placeholder="Escriu grup">
              <input type="hidden" name="tipus" value="2">
              <input type="hidden" name="t" value="1">
            </div>
            <button type="submit" class="btn btn-primary">Insereix</button>
          </form>
          <?php
          if ($t == "1") {
            echo $msg;
          }
          ?>
        </div>
      </div>
    </div>
  </body>

<?php
} elseif ($tipus == '3') {
  if ($paraula != "" && $t == "1") {

    // $connection = new MongoDB\Driver\Manager("mongodb://localhost:27017");
    // $filtro = ['paraulaProhibida.paraula' => "'".$paraula."'"];
    // $opcions = [];
    // $query = new MongoDB\Driver\Query($filtro, $opcions);
    // $result = $connection->executeQuery('db.paraulesDites', $query);
    // $msg = "";
    // foreach ($result as $document) {
    //   $msg = "<p class='text-danger'> La paraula <span class='font-weight-bold'>" . $paraula . "</span> l'ha dits <span class='font-weight-bold'>" . $document->nomEmisor . "</span> el <span class='font-weight-bold'>" . $document->dataHora . "</span></p>";
    // }

    $sql = "SELECT * FROM DEL_paraula_usuari INNER JOIN DEL_usuaris ON DEL_usuaris.id = DEL_paraula_usuari.id_usuari_emiso WHERE paraula = '$paraula'";

    $result = $con->query($sql);
    if ($result->num_rows > 0) {
      $msg = "<table class='table table-striped table-bordered table-hover m-3'>" .
        "<thead class='thead-dark'>" .
        "<tr>" .
        "<th>Usuari</th>" .
        "<th>Paraula</th>" .
        "<th>Data i hora</th>" .
        "</tr>" .
        "</thead>" .
        "<tbody>";
      while ($row = $result->fetch_assoc()) {
        $id_usuari  = $row['id_usuari_emiso'];
        $usuari     = $row['nom'];
        $dni        = $row['dni'];
        $id_paraula = $row['id_paraula'];
        $paraula    = $row['paraula'];
        $datahora   = $row['data_hora'];
        $msg .= "<tr>" .
          "<td>" . $id_usuari . " - " . $usuari . " </br> " . $dni . "</td>" .
          "<td>" . $id_paraula . " - " . $paraula . "</td>" .
          "<td>" . $datahora . "</td>" .
          "</tr>";
      }
      $msg .= "</tbody>" .
        "</table>";
    } else {
      $msg = "<p class='text-danger'> Ningú ha dit <span class='font-weight-bold'>" . $paraula . "</span>. </p>";
    }
  } else if ($usuari != "" && $t == "2") {
    $sql = "SELECT * FROM DEL_paraula_usuari INNER JOIN DEL_usuaris ON DEL_usuaris.id = DEL_paraula_usuari.id_usuari_emiso WHERE DEL_usuaris.dni = '$usuari'";

    $result = $con->query($sql);
    if ($result->num_rows > 0) {
      $msg = "<table class='table table-striped table-bordered table-hover m-3'>" .
        "<thead class='thead-dark'>" .
        "<tr>" .
        "<th>Usuari</th>" .
        "<th>Paraula</th>" .
        "<th>Data i hora</th>" .
        "</tr>" .
        "</thead>" .
        "<tbody>";
      while ($row = $result->fetch_assoc()) {
        $id_usuari  = $row['id_usuari_emiso'];
        $usuarinom     = $row['nom'];
        $dni        = $row['dni'];
        $id_paraula = $row['id_paraula'];
        $paraula    = $row['paraula'];
        $datahora   = $row['data_hora'];
        $msg .= "<tr>" .
          "<td>" . $id_usuari . " - " . $usuarinom . " </br> " . $dni . "</td>" .
          "<td>" . $id_paraula . " - " . $paraula . "</td>" .
          "<td>" . $datahora . "</td>" .
          "</tr>";
      }
      $msg .= "</tbody>" .
        "</table>";
    } else {
      $msg = "<p class='text-danger'> L'usuari <span class='font-weight-bold'>" . $usuari . "</span> no ha dit cap paraula prohibida. </p>";
    }
  }
?>

  <body>
    <nav class="m-3" aria-label="Page navigation example">
      <ul class="pagination">
        <li class="page-item"><a class="page-link" href="index.php">Torna</a></li>
      </ul>
    </nav>
    <div class="d-flex justify-content-center">
      <div class="card m-3" style="width: 18rem;">
        <div class="card-body">
          <form>
            <div class="form-group">
              <label>Consulta qui ha dit la paraula malsonant</label>
              <input type="text" class="form-control" name="paraula" placeholder="Escriu la paraula">
              <input type="hidden" name="tipus" value="3">
              <input type="hidden" name="t" value="1">
            </div>
            <button type="submit" class="btn btn-primary">Cerca</button>
          </form>

        </div>

      </div>
      <div class="card m-3" style="width: 18rem;">
        <div class="card-body">
          <form>
            <div class="form-group">
              <label>Consulta quines paraules malsonants ha dit un usuari</label>
              <input type="text" class="form-control" name="usuari" placeholder="Escriu l'usuari">
              <input type="hidden" name="tipus" value="3">
              <input type="hidden" name="t" value="2">
            </div>
            <button type="submit" class="btn btn-primary">Cerca</button>
          </form>

        </div>

      </div>
      <?php
      if ($t == "1") {
        echo $msg;
      } elseif ($t == "2") {
        echo $msg;
      }

      ?>
    </div>
  </body>
<?php

} elseif ($tipus == '') {

?>



  <body>
    <h1 class="text-center">PARAULES PROHIBIDES</h1>

    <div class="d-flex justify-content-center">
      <div class="card m-3" style="width: 18rem;">
        <div class="card-body">
          <h5 class="card-title">Consultar Paraules</h5>
          <p class="card-text">Fer una consulta de les paraules prohibides que es troben a la base de dades.</p>
          <a href="index.php?tipus=1" class="btn btn-primary">Consulta</a>
        </div>
      </div>
      <div class="card m-3" style="width: 18rem;">
        <div class="card-body">
          <h5 class="card-title">Inserir Paraules</h5>
          <p class="card-text">Fer una insercio de noves paraules prohibides que no es troben a la base de dades.</p>
          <a href="index.php?tipus=2" class="btn btn-primary">Inserir</a>
        </div>
      </div>
      <div class="card m-3" style="width: 18rem;">
        <div class="card-body">
          <h5 class="card-title">Registre de paraules dites</h5>
          <p class="card-text">Consultar les paraules prohibides dites i les persones que les han dit.</p>
          <a href="index.php?tipus=3" class="btn btn-primary">Consulta</a>
        </div>
      </div>
    </div>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
  </body>

</html>

<?php
}
?>