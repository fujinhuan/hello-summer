<?php

$data = array();
for ($x = 0; $x < 1; $x++) {
    $id = '100' . $x . '-' . $x;
    array_push($data, array(
        'id' => $id,
        'title' => '2019秋季高领拼接' . $id,
        'price' => '100.00',
        'stock' => '500',
        'code' => 'HY19-SW' . rand(100, 999),
        'img' => '03.jpg',
    ));
}

$res = array('data' => $data);
// $res = array('data' => array());
echo json_encode($res);
