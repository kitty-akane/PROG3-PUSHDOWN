INSERT INTO
    invoice (customer_name, status)
VALUES ('Alice', 'CONFIRMED'),
    ('Bob', 'PAID'),
    ('Charlie', 'DRAFT');

INSERT INTO
    invoice_line (
        invoice_id,
        label,
        quantity,
        unit_price
    )
VALUES (1, 'Produit A', 2, 100),
    (1, 'Produit B', 1, 50),
    (2, 'Produit A', 5, 100),
    (2, 'Service C', 1, 200),
    (3, 'Produit B', 3, 50);

-- Q1 :
SELECT i.id, i.customer_name, SUM(il.quantity * il.unit_price) AS total
FROM invoice i
    JOIN invoice_line il ON il.invoice_id = i.id
GROUP BY
    i.id,
        i.customer_name
ORDER BY
    i.id ASC;

-- Q2 :
SELECT i.id, i.customer_name, i.status, SUM(il.quantity * il.unit_price) AS total
FROM invoice i
    JOIN invoice_line il ON il.invoice_id = i.id
WHERE
    i.status IN ('CONFIRMED', 'PAID')
GROUP BY
    i.id,
    i.customer_name,
    i.status;

-- Q3 :
SELECT
    SUM(
        CASE
            WHEN i.status = 'PAID' THEN il.quantity * il.unit_price
            ELSE 0
        END
    ) AS total_paid,
    SUM(
        CASE
            WHEN i.status = 'CONFIRMED' THEN il.quantity * il.unit_price
            ELSE 0
        END
    ) AS total_confirmed,
    SUM(
        CASE
            WHEN i.status = 'DRAFT' THEN il.quantity * il.unit_price
            ELSE 0
        END
    ) AS total_draft
FROM invoice i
    JOIN invoice_line il ON il.invoice_id = i.id;

-- Q4 :
SELECT SUM(
        il.quantity * il.unit_price * CASE i.status
            WHEN 'PAID' THEN 1.00
            WHEN 'CONFIRMED' THEN 0.50
            WHEN 'DRAFT' THEN 0.00
            ELSE 0.00
        END
    ) AS total_with_tax
FROM invoice i
JOIN invoice_line il ON il.invoice_id = i.id;

-- Q5-A : Totaux HT, TVA et TTC par facture
SELECT
    i.id,
    SUM(il.quantity * il.unit_price) AS ht,
    SUM(il.quantity * il.unit_price * tc.rate / 100) AS tva,
    SUM(il.quantity * il.unit_price * (1 + tc.rate / 100)) AS ttc
FROM invoice i
JOIN invoice_line il ON il.invoice_id = i.id
CROSS JOIN tax_config tc
WHERE tc.label = 'TVA STANDARD'
GROUP BY i.id
ORDER BY i.id ASC;

-- Q5-A : Totaux HT, TVA et TTC par facture
SELECT
    i.id,
    SUM(il.quantity * il.unit_price) AS ht,
    SUM(il.quantity * il.unit_price * tc.rate / 100) AS tva,
    SUM(il.quantity * il.unit_price * (1 + tc.rate / 100)) AS ttc
FROM invoice i
JOIN invoice_line il ON il.invoice_id = i.id
CROSS JOIN tax_config tc
WHERE tc.label = 'TVA STANDARD'
GROUP BY i.id
ORDER BY i.id ASC;

-- Q5-B : Chiffre d'affaires TTC pondéré
SELECT SUM(
    il.quantity * il.unit_price * (1 + tc.rate / 100) *
    CASE i.status
        WHEN 'PAID'      THEN 1.00
        WHEN 'CONFIRMED' THEN 0.50
        WHEN 'DRAFT'     THEN 0.00
        ELSE 0.00
    END
) AS weighted_turnover_ttc
FROM invoice i
JOIN invoice_line il ON il.invoice_id = i.id
CROSS JOIN tax_config tc
WHERE tc.label = 'TVA STANDARD';

