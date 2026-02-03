<?php

namespace App\Service;

use App\Repository\ManagerRepository;

class EnterpriseCodeGenerator
{
    public function __construct(
        private readonly ManagerRepository $managerRepository
    ) {
    }

    /**
     * Generates a unique, readable enterprise code.
     * Format: 3 uppercase letters + 4 digits (e.g., "ABC1234")
     */
    public function generate(): string
    {
        do {
            // Generate 3 random uppercase letters
            $letters = '';
            for ($i = 0; $i < 3; $i++) {
                $letters .= chr(65 + random_int(0, 25)); // A-Z
            }

            // Generate 4 random digits
            $digits = str_pad((string) random_int(0, 9999), 4, '0', STR_PAD_LEFT);

            $code = $letters . $digits;
        } while ($this->managerRepository->findOneBy(['enterpriseCode' => $code]) !== null);

        return $code;
    }
}
