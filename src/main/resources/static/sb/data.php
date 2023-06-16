<?php

$nums = $_POST['nums'];
$index = $_POST['page'];
$query = $_POST['query'];

$count = 180;
if ($query != '') {
    $count = 101;
}
$total = ceil($count / $nums);
$max = $nums;
if ($index == $total) {
    $max = $nums - $count % $nums;
}

$data = array();
for ($x = 0; $x < $max; $x++) {
    $id = '100' . strlen($query) . $index . '-' . $x;
    array_push($data, array(
        'id' => $id,
        'title' => '2019秋季高领#' . $query . '#拼接' . $id,
        'price' => '100.00',
        'stock' => '500',
        'code' => 'HY19-SW' . rand(100, 999),
        'img' => '03.jpg',
    ));
}

$res = array('index' => $index, 'total' => $total, 'data' => $data);

echo json_encode($res);
